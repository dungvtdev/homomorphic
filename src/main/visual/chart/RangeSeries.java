package visual.chart;

/**
 * Created by dung on 01/12/2016.
 */
public class RangeSeries extends Series {
    private int[] gaps = new int[]{1,5,10,50,100,500,1000};

    public float min;
    public float max;

    public RangeSeries(String label, float min, float max){
        super(label);
        this.min = min;
        this.max = max;
    }

    @Override
    public SeriesLabels getSeriesLabels(int maxLength) {
        float maxGap = gaps[gaps.length-1];

        double amount = (max - min)/(maxLength -2);
        double scale = 1;
        // find gap
        while(amount > maxGap) {
            amount /= maxGap;
            scale*=maxGap;
        }
        // tim prototype gap >= amount/maxlength
        double tgap = amount;//(int)(amount/(maxLength-2));
        for(int i=0;i<gaps.length;i++){
            if(gaps[i]>tgap){
                tgap=gaps[i];
                break;
            }
        }

        double gap = tgap*scale;

        //find min
        double gmin = Math.floor(min/gap)*gap;

        // calc labels
        int nLabels = (int)Math.ceil((max-gmin)/gap);
        String[] labels = new String[nLabels];
        double[] values = new double[nLabels];
        for(int i=0;i<nLabels;i++){
            values[i] = gmin+i*gap;
            labels[i] = String.format("%.0f", values[i]);
        }
        return new SeriesLabels(labels, values);

//        int gmin = (int)gapValue(min);
//        float minGap = (max-gmin)/maxLength;
//        float gap = 0;
//        if(minGap>maxGap){
//            int ratio = (int)Math.ceil(minGap/maxGap);
//            gap = (int)(maxGap*gapValue(ratio));
//        }else{
////            gap = gaps[gaps.length-1];
//            for(int i=gaps.length -1;i>=0;i--){
//                int num = (int)Math.ceil((max-gmin)/gaps[i]);
//                if(num > maxLength) break;
//                gap = gaps[i];
//            }
//        }
//
//        // calc labels
//        int nLabels = (int)Math.ceil((max-gmin)/gap)+1;
//        String[] labels = new String[nLabels];
//        double[] values = new double[nLabels];
//        for(int i=0;i<nLabels;i++){
//            values[i] = gmin+i*gap;
//            labels[i] = String.format("%.0f", values[i]);
//        }
//        return new SeriesLabels(labels, values);
    }

    @Override
    public double getMax() {
        return this.max;
    }

//    private double gapValue(double value){
//        int maxGap = gaps[gaps.length-1];
//        double num = value;
//        double scale = 1;
//        while(value > maxGap) {
//            num /= maxGap;
//            scale*=maxGap;
//        }
//        for(int i=gaps.length-1;i>=0;i--){
//            if(num>gaps[i]){
//                num=gaps[i];
//                break;
//            }
//        }
//        return num*scale;
//    }
}
