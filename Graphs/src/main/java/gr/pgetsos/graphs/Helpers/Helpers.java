package gr.pgetsos.graphs.Helpers;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Map;

public class Helpers {
	public static Map<Integer, Double> getQoEMap(String folder) {
		if (folder.contains("10s")) {
			return Map.ofEntries(Map.entry(45373, 0.742), Map.entry(88482, 0.875), Map.entry(127412, 0.913), Map.entry(176780, 0.913),
					Map.entry(216536, 0.928), Map.entry(252988, 0.938), Map.entry(317328, 0.949), Map.entry(368912, 0.955),
					Map.entry(503270, 0.953), Map.entry(568500, 0.957), Map.entry(771359, 0.945), Map.entry(987061, 0.955),
					Map.entry(1174238, 0.961), Map.entry(1431232, 0.967), Map.entry(2070985, 0.958), Map.entry(2384387, 0.963),
					Map.entry(2884382, 0.968), Map.entry(3245900, 0.971), Map.entry(3493765, 0.973), Map.entry(3792491, 0.975));
		} else {
			return Map.ofEntries(Map.entry(45652, 0.742), Map.entry(89283, 0.876), Map.entry(131087, 0.916), Map.entry(178351, 0.914),
					Map.entry(221600, 0.930), Map.entry(262537, 0.940), Map.entry(334349, 0.951), Map.entry(396126, 0.958),
					Map.entry(522286, 0.954), Map.entry(595491, 0.959), Map.entry(791182, 0.946), Map.entry(1032682, 0.957),
					Map.entry(1244778, 0.963), Map.entry(1546902, 0.969), Map.entry(2133691, 0.959), Map.entry(2484135, 0.964),
					Map.entry(3078587, 0.970), Map.entry(3526922, 0.973), Map.entry(3840360, 0.975), Map.entry(4219897, 0.978));
		}
	}

	public static double getHossIndex(double... qoe) {
		DescriptiveStatistics data = new DescriptiveStatistics();
		for (double v : qoe) {
			data.addValue(v);
		}
		//double std = data.getStandardDeviation(); // This is for sample std - wrong results
		double std = Math.sqrt(data.getPopulationVariance());
		double h = 0.978;
		double l = 0.742;

		return 0.5 * (1 - ((2*std)/(h-l))) + 0.5 * (data.getSum()/(h*data.getN()));
	}
}
