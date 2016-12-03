package visual.chart;

public class ScaleRange{
    private double min;
    private double max;

    private double toMin;
    private double toMax;

    private double scale;

    public ScaleRange(double min, double max, double toMin, double toMax){
        this.min = min;
        this.max = max;
        this.toMin = toMin;
        this.toMax = toMax;
        this.scale = (toMax-toMin)/(max-min);
    }

    public double scaleValue(double value){
        return toMin + (value-min)*scale;
    }

    public double getScale(){
        return this.scale;
    }
}