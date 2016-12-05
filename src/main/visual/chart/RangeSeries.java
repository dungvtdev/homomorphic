package visual.chart;

/**
 * Created by dung on 01/12/2016.
 */
public class RangeSeries extends Series {
    private int[] def_gaps = new int[]{1,5,10,50,100,500,1000};

    public double min;
    public double max;

    public RangeSeries(String label, double min, double max){
        super(label);
        this.min = min;
        this.max = max;
    }


    @Override
    public SeriesLabels getSeriesLabels(int maxLength) {
        double nmin = min;
        double nmax = max;
        if(min<0){
            nmin = 0;
            nmax = Math.max(-min, max);
        }
        int nMaxLength = (int)Math.ceil((nmax-nmin)/(max - min)*maxLength);

        //TODO kiem tra nMaxLength < 2
        double amount = (nmax - nmin)/nMaxLength;
        boolean labelFloat = false;

        float[] gaps=new float[def_gaps.length];
        if(amount>1){
            for(int i=0;i<def_gaps.length;i++)
                gaps[i] = def_gaps[i];
            labelFloat = false;
        }else{
            for(int i=0;i<def_gaps.length;i++)
                gaps[i] = 1.0f/def_gaps[def_gaps.length-1-i];
            labelFloat = true;
        }
        float maxGap = gaps[gaps.length-1];

        double scale = 1;
        // find gap
        while(amount > maxGap) {
            amount /= maxGap;
            scale*=maxGap;
        }

        // tim prototype gap >= amount/maxlength
        double tgap = amount;
        for(int i=0;i<gaps.length;i++){
            if(gaps[i]>tgap){
                tgap=gaps[i];
                break;
            }
        }

        double gap = tgap*scale;

        if(min<0){
            // lam phan am
            int nNegValues = (int)Math.ceil(-min/gap);
            int nPosValues = (int)Math.ceil((max)/gap)+1;             //them 1 de dien max vao cuoi

            double[] values = new double[nNegValues+nPosValues];      // lap lai so 0, nhung cong them min
            String[] labels = new String[nNegValues+nPosValues];
            values[0] = min;
            int index = 1;
            for(int i=-nNegValues+1;i<nPosValues-1;i++,index++){
                values[index] = i*gap;
            }
            values[index] = max;
            for(int i=0;i<values.length;i++){
                if(labelFloat)
                    labels[i] = String.format("%.4f", values[i]);
                else
                    labels[i] = String.format("%.0f", values[i]);
            }
            return new SeriesLabels(labels, values);

        }else{
            //find min
            double gmin = Math.floor(nmin/gap)*gap;
            // calc labels
            int nLabels = (int)Math.ceil((nmax-gmin)/gap)+1;        //them 1 de dien max vao cuoi
            String[] labels = new String[nLabels];
            double[] values = new double[nLabels];
            if(gmin<nmin){
                values[0]=nmin;
            }else{
                values[0]=gmin;
            }
            for(int i=1;i<nLabels-1;i++){
                values[i] = gmin+i*gap;
            }
            values[nLabels-1]=max;
            for(int i=0;i<nLabels;i++){
                if(labelFloat)
                    labels[i] = String.format("%.4f", values[i]);
                else
                    labels[i] = String.format("%.0f", values[i]);
            }
            return new SeriesLabels(labels, values);
        }

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

    @Override
    public double getMin() {
        return this.min;
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
