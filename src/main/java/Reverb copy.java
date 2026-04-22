package main.java;

public class Reverb {
    //找到峰值
    public static double samplePeak(double []s){
        double peak = 0.0;
        for (double v : s)  peak = Math.max(peak, v);
        return peak;
    }

    //峰值归一化：
    public static double[] clipPeakNormalizeMy(double []s){
        double peak = samplePeak(s);        //找到peak
        double k = 1;
        double []y = new double[s.length];
        if (peak >= 1.0){
            k = 0.99 / peak;       //直接改变k：(0.99/ peak*k) * peak;
        }
        for (int i = 0; i < s.length; ++i){
            y[i] = s[i] * k;     //这样x[i] * k就永远不会超过0.99
        }
        return y;
    }
    
    public static void main(String[] args) {
        double[] x = StdAudio.read(args[0]);
        double delay = 0.04;  // 延迟时间（秒）
        double a = 0.33;     // 衰减系数
        int deSam = (int) (delay * 44100);        //延迟样本
        int n = x.length;
        int K = 7;      //最多累加次数（最多回音叠加次数）
        double []y = new double[n];
        //混响滤镜：
        double []sum = new double[n];
        for (int i = 0; i < n; ++i){
            //累积：
            double weight = 1;
            int k = 0;
            for (int j = i; j >= 0; j = j - deSam){
                if (k > K) break;   //不能超过最多累加次数
                sum[i] += x[j] * weight;
                weight *= a;
                ++k;
            }
        }
        //峰值归一化，调用clipPeakNormalizeMy()函数：
        y = clipPeakNormalizeMy(sum);
        StdAudio.save(args[1], y);
    }
}