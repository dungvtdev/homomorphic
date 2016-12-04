package visual.chart;

public class ScaleRange{
    private double min;
    private double max;

    private double toMin;
    private double toMax;

    private double scale;
    private double inverseScale;

    public ScaleRange(double min, double max, double toMin, double toMax){
        this.min = min;
        this.max = max;
        this.toMin = toMin;
        this.toMax = toMax;
        this.scale = (toMax-toMin)/(max-min);
        this.inverseScale = 1/this.scale;
    }

    public static void expandRange(ScaleRange range, double toMin, double toMax){
        range.min = range.inverseScaleValue(toMin);
        range.max = range.inverseScaleValue(toMax);
        range.toMin = toMin;
        range.toMax = toMax;
    }

    public double scaleValue(double value){
        return toMin + (value-min)*scale;
    }

    public double inverseScaleValue(double value){
        return min + (value-toMin)* getInverseScale();
    }

    public double getScale(){
        return this.scale;
    }

    public double getInverseScale(){
        return this.inverseScale;
    }

    public double scaleValue(String vstr){
        if(vstr.equals("max")){
            return toMax;
        }
        if(vstr.equals("min")){
            return toMin;
        }
        return 0;
    }
}