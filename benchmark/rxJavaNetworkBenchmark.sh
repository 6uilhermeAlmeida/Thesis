mkdir results
adb -d shell am instrument -w -r -e debug false -e class 'com.example.benchmark.rxjava.network.RxJavaNetworkBenchmark' com.example.benchmark.test/androidx.benchmark.junit4.AndroidBenchmarkRunner | egrep "display|median|min|standardDeviation|mean" > results/rxjava_network.txt
