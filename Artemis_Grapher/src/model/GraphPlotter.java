package model;

/**
 * This class has been created to abstract
 * chronograms representation
 * by automatically adding
 * UP and DOWN position to a graph
 * @author oliviercros
 *
 */
public class GraphPlotter {
	/**
	 * Range tick value
	 */
	public static final int RANGETICK = 2;
	
	public double graphSize;
	private GraphPlots plots;
	
	public GraphPlotter(double graphSizeP) {
		graphSize = graphSizeP;
		plots = new GraphPlots();
	}
	
	public void addPoint(double timeValue, GraphPosition position, String key) {
		if(position == GraphPosition.UP) {
			 plots.get(key).add(new GraphPlot(
					  timeValue, graphSize));
		}
		
		if(position == GraphPosition.DOWN) {
			 plots.get(key).add(new GraphPlot(
					 timeValue, graphSize-RANGETICK));
		}
	}
	
	public GraphPlots getPlots() {
		return this.plots;
	}
}
