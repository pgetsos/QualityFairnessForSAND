import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;


public class Main {
	private static final Logger logger = LogManager.getLogger("Main");

	public static void main(String[] args) {
		List<String> list = List.of("240", "360", "480", "720", "1080");
		List<Double> vars240 = List.of(-21.756, -1.181, 0.9846);
		List<Double> vars360 = List.of(-17.53, -1.048, 0.9912);
		List<Double> vars480 = List.of(-13.303, -0.914, 0.9977);
		List<Double> vars720 = List.of(-4.85, -0.647,  1.011);
		List<Double> vars1080 = List.of(-3.035, -0.5061,  1.022);
//		List<Integer> bits240 = List.of(50, 100, 150);
//		List<Integer> bits360 = List.of(200, 250, 300, 400, 500);
//		List<Integer> bits480 = List.of(600, 700);
//		List<Integer> bits720 = List.of(900, 1200, 1500, 2000);
//		List<Integer> bits1080 = List.of(2500, 3000, 4000, 5000, 6000, 8000);
		List<Integer> bits240 = List.of(45652, 89283, 131087);
		List<Integer> bits360 = List.of(178351, 221600, 262537, 334349, 396126);
		List<Integer> bits480 = List.of(522286, 595491);
		List<Integer> bits720 = List.of(791182, 1032682, 1244778, 1546902);
		List<Integer> bits1080 = List.of(2133691, 2484135, 3078587, 3526922, 3840360, 4219897);

		Map<String, List<Double>> vars = Map.of("240", vars240, "360", vars360, "480", vars480, "720", vars720, "1080", vars1080);
		Map<String, List<Integer>> bits = Map.of("240", bits240, "360", bits360, "480", bits480, "720", bits720, "1080", bits1080);

		String s = "(";
		for (String res : list) {
			List<Double> variables = vars.get(res);
			List<Integer> bitrates = bits.get(res);
			double a = variables.get(0);
			double b = variables.get(1);
			double c = variables.get(2);

			for (Integer bitrate : bitrates) {
				double resultK = a*(Math.pow(bitrate/1000, b)) + c;
				s = s + "Map.entry("+bitrate*1000 + ", " + new DecimalFormat("#0.000").format(resultK) + "), ";
				logger.info("Result {} for bitrate in kbps {}", () -> new DecimalFormat("#0.000").format(resultK), () -> bitrate);
			}
		}
		logger.info(s+")");
	}
}