package edu.byu.testare.cs246multi_threadedprogramming;


import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The class for the main activitiy of the app
 */
public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progressCheck;
    private Handler mHandler = new Handler();

    private synchronized void setBarProgress(int prog) {
        progressCheck = Math.min(100,prog);
    }
    private synchronized int getBarProgress() {
        return progressCheck;
    }

    private static final String FILE_NAME = "file.file";

    private ArrayAdapter<String> listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final Context context = this;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ListView listView = (ListView)findViewById(R.id.listView);
        listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        listView.setAdapter(listAdapter);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        /*Create button event listener*/
        findViewById(R.id.createButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            File file = new File(context.getFilesDir(), FILE_NAME);
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            PrintWriter fileOut = new PrintWriter(new BufferedWriter(new FileWriter(file, false)));

                            for (int i = 1; i <= 10; ++i) {
                                fileOut.println(i);
                                setBarProgress(i * 10);
                                System.out.println("-" + i);

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setProgress(getBarProgress());
                                    }
                                });
                                Thread.sleep(250);
                            }

                            fileOut.close();
                        } catch (IOException | InterruptedException ioe) {
                            ioe.printStackTrace(System.err);
                        }
                    }
                });
                thread.start();
            }
        });


        /*Load button*/
        findViewById(R.id.loadButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File f = new File(context.getFilesDir(), FILE_NAME);
                            Scanner fileIn = new Scanner(f);
                            setBarProgress(0);
                            System.out.println("!..!" + f.exists());
                            final List<String> loadedList = new ArrayList<>();
                            while (fileIn.hasNextLine()) {
                                String s = fileIn.nextLine();
                                System.out.println("+" + s);
                                loadedList.add(s);
                                setBarProgress(getBarProgress()+10);
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setProgress(getBarProgress());
                                    }
                                });
                                Thread.sleep(250);
                            }

                            fileIn.close();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listAdapter.clear();
                                    listAdapter.addAll(loadedList);
                                    loadedList.clear();
                                    listAdapter.notifyDataSetChanged();
                                }
                            });

                        } catch (IOException | InterruptedException ioe) {
                            ioe.printStackTrace(System.err);
                        }
                    }
                });
                thread.start();
            }
        });

        /*Clear button*/
        findViewById(R.id.clearButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listAdapter.clear();
                        listAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
