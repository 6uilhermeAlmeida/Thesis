mkdir results
adb -d shell am instrument -w -r -e debug false -e class 'com.example.benchmark.coroutines.database.CoroutinesDatabaseBenchmark' com.example.benchmark.test/androidx.benchmark.junit4.AndroidBenchmarkRunner | egrep "display|median|min|standardDeviation|mean" > results/coroutines_database.txt
