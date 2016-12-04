package app;

import java.util.List;

/**
 * Created by dung on 03/12/2016.
 */
public interface HomomorphicProcessListener{
    void onProcessReturn(boolean success,List<double[]> result, int offset);
}
