# Android Workmanager

What is WorkManager?
WorkManager is part of Android Jetpack. WorkManager helps us to execute our tasks immediately or an appropriate time.

Earlier we had AlarmManager, JobScheduler, FirebaseJobDispatcher for scheduling the background tasks. But the issues were

And it is very simple, just go to app level build.gradle file and add the following implementation line.
 
    implementation "android.arch.work:work-runtime:1.0.1"
        
 
For creating a worker we need to create a class and then in the class we need to extend Worker. So here I am creating a class named MyWorker.

 
    import android.app.NotificationChannel;
    import android.app.NotificationManager;
    import android.content.Context;
    import android.support.annotation.NonNull;
    import android.support.v4.app.NotificationCompat;
 
    import androidx.work.Worker;
    import androidx.work.WorkerParameters;
 
    public class MyWorker extends Worker {
 
    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
 
    /*
    * This method is responsible for doing the work
    * so whatever work that is needed to be performed
    * we will put it here
    * 
    * For example, here I am calling the method displayNotification()
    * It will display a notification
    * So that we will understand the work is executed 
    * */
    
    @NonNull
    @Override
    public Result doWork() {
        displayNotification("My Worker", "Hey I finished my work");
        return Result.sucess();
    }
 
    /*
    * The method is doing nothing but only generating 
    * a simple notification
    * If you are confused about it
    * you should check the Android Notification Tutorial
    * */
    private void displayNotification(String title, String task) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
 
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("simplifiedcoding", "simplifiedcoding", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
 
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "simplifiedcoding")
                .setContentTitle(title)
                .setContentText(task)
                .setSmallIcon(R.mipmap.ic_launcher);
 
        notificationManager.notify(1, notification.build());
      }
    }    
Now let’s perform the work that we created. For this first come inside MainActivity.java and write the following code.
   
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

        // It is needed to perform the work periodically for example taking backup to the server. In scenarios like this we can use PeriodicWorkRequest class.    Everything else is same.
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

        }
    }
 
 ##### Any Queries? or Feedback, please let me know by opening a [new issue](https://github.com/myinnos/android-workmanager/issues/new)!

## Contact
#### Prabhakar Thota
* :globe_with_meridians: Website: [myinnos.in](http://www.myinnos.in "Prabhakar Thota")
* :email: e-mail: contact@myinnos.in
* :mag_right: LinkedIn: [PrabhakarThota](https://www.linkedin.com/in/prabhakarthota "Prabhakar Thota on LinkedIn")
* :thumbsup: Twitter: [@myinnos](https://twitter.com/myinnos "Prabhakar Thota on twitter")    
* :camera: Instagram: [@prabhakar_t_](https://www.instagram.com/prabhakar_t_/ "Prabhakar Thota on Instagram")   

> If you appreciate my work, consider buying me a cup of :coffee: to keep me recharged :metal: by [PayPal](https://www.paypal.me/fansfolio)

License
-------

    Copyright 2021 MyInnos

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
