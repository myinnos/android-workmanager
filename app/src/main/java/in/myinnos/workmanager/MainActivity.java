package in.myinnos.workmanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import in.myinnos.workmanager.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //creating a data object
        //to pass the data with workRequest
        //we can put as many variables needed
        Data data = new Data.Builder()
                .putString(MyWorker.TASK_TITLE, "MyInnos")
                .putString(MyWorker.TASK_DESC, "The task data passed from MainActivity")
                .build();

        //creating constraints
        // you can add as many constraints as you want
        Constraints constraints =
                new Constraints.Builder()
                        .setRequiresCharging(true)
                        .build();

        // It is needed to perform the work periodically for example taking backup to the server. In scenarios like this we can use PeriodicWorkRequest class. Everything else is same.
        // note: PeriodicWorkRequest not working, yet to fix
        final PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(MyWorker.class, 10000, TimeUnit.SECONDS)
                        .setInputData(data)
                        .build();

        //This is the subclass of our WorkRequest
        final OneTimeWorkRequest workRequest =
                new OneTimeWorkRequest.Builder(MyWorker.class)
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build();

        binding.btEnqueue.setOnClickListener(v -> WorkManager.getInstance().enqueue(workRequest));

        //Listening to the work status
        WorkManager.getInstance().getWorkInfoByIdLiveData(workRequest.getId())
                .observe(this, workInfo -> {
                    //receiving back the data
                    if (workInfo != null)
                        binding.txStatus.append(workInfo.getState() + "\n");
                });


        // --- We can also cancel the work if required. For this we have a method cancelWorkById(). It takes the work id as an argument that we can get from our WorkRequest object.
        //WorkManager.getInstance().cancelWorkById(workRequest.getId());

    }
}