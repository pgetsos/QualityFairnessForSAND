__author__ = 'pgetsos'

import config_dash
import socket
from socket import timeout as TimeoutException

HOST = "10.0.0.6"
PORT = 3535


def sand(segment_number, bitrates, average_dwn_time, recent_download_sizes, previous_segment_times,
         current_bitrate, ip, recent_segment_qualities, quality_score_bitrates, last, fair, buffer_size):
    """
    Module to predict the next_bitrate using the basic_dash algorithm. Selects the bitrate that is one lower than the
    current network capacity.
    :param segment_number: Current segment number
    :param bitrates: A tuple/list of available bitrates
    :param average_dwn_time: Average download time observed so far
    :param segment_download_time:  Time taken to download the current segment
    :return: next_rate : Bitrate for the next segment
    :return: updated_dwn_time: Updated average download time
    """
    # Truncating the list of download times and segment sizes
    while len(previous_segment_times) > 1:
        previous_segment_times.pop(0)
    while len(recent_download_sizes) > 1:
        recent_download_sizes.pop(0)
    while len(recent_segment_qualities) > config_dash.BASIC_DELTA_COUNT:
        recent_segment_qualities.pop(0)
    if len(previous_segment_times) == 0 or len(recent_download_sizes) == 0:
        updated_dwn_time = -1
        download_rate = -1
    else:
        updated_dwn_time = sum(previous_segment_times) / len(previous_segment_times)
        config_dash.LOG.debug("The average download time upto segment {} is {}. Before it was {}".format(segment_number,
                                                                                                         updated_dwn_time,
                                                                                                         average_dwn_time))
        # Calculate the running download_rate in Kbps for the most recent segments
        download_rate = sum(recent_download_sizes) * 8 / (updated_dwn_time * len(previous_segment_times))

    total_quality_score = -1
    if len(recent_segment_qualities) != 0:
        total_quality_score = calculate_avg_quality_score(recent_segment_qualities)
    bitrates.sort()
    next_rate = bitrates[0]

    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_socket.connect((HOST, PORT))
    client_socket.settimeout(2)

    ip_message = "IP: " + ip
    bandwidth_message = "Bandwidth: " + str(download_rate)
    level_message = "Level: " + str(current_bitrate)
    buffer_message = "Buffer: " + str(buffer_size)
    quality_message = "Quality: " + str(total_quality_score)
    request_message = "Requesting bandwidth"
    if fair == "true":
        request_message = "Requesting fairness"

    send_status_message(client_socket, ip_message)

    if segment_number == 1:
        qlist_string = quality_list_to_string(bitrates, quality_score_bitrates)
        qlist_message = "QLIST: " + qlist_string
        send_status_message(client_socket, qlist_message)

    send_status_message(client_socket, bandwidth_message)
    send_status_message(client_socket, level_message)
    send_status_message(client_socket, buffer_message)
    send_status_message(client_socket, quality_message)
    requested_rate = send_request_message(client_socket, request_message)

    end_message = "Over" + "\r\n"
    if last:
        end_message = "LastOver" + "\r\n"

    client_socket.sendall(end_message.encode("UTF-8"))

    best_bitrate = float(requested_rate)

    for index, bitrate in enumerate(bitrates[1:], 1):
        if best_bitrate > bitrate * 0.999:
            next_rate = bitrate
        else:
            break

    config_dash.LOG.info("SAND Adaptation: Download Rate = {}, next_bitrate = {}, suggested bitrate = {}".format(download_rate, next_rate, best_bitrate))
    return next_rate, updated_dwn_time


def send_status_message(client_socket, message):
    data = ''
    message = message + "\r\n"
    while data != "Received":
        client_socket.sendall(message.encode("UTF-8"))
        try:
            data = client_socket.recv(1024).decode("UTF-8").strip("\r\n")
        except TimeoutException:
            print("Timeout!!! Trying again...")
            continue


def send_request_message(client_socket, message):
    data = ''
    message = message + "\r\n"
    while not data.startswith("Received"):
        client_socket.sendall(message.encode("UTF-8"))
        print(data)
        try:
            data = client_socket.recv(1024).decode("UTF-8").strip("\r\n")
        except TimeoutException:
            print("Timeout!!! Trying again...")
            continue
    return data.strip("Received")


def calculate_avg_quality_score(quality_scores):
    total = 0
    for score in quality_scores:
        total = total + score
    return total/len(quality_scores)


def quality_list_to_string(bitrates, quality_score_bitrates):
    final_string = ''
    for bitrate, score in zip(bitrates, quality_score_bitrates):
        final_string += "NEW" + str(bitrate) + ":" + str(quality_score_bitrates[score])
    return final_string
