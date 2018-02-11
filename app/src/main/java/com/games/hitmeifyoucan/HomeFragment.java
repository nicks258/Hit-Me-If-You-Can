package com.games.hitmeifyoucan;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.plattysoft.leonids.ParticleSystem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.media.MediaExtractor.MetricsConstants.FORMAT;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    View view;
    TextView score;
    Button hitMe;
    private String EVENT_DATE_TIME = "2018-12-31 10:30:00";
    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private LinearLayout linear_layout_1, linear_layout_2;
//    private TextView tv_days, tv_hour;
    private TextView  tv_second;
    private Handler handler = new Handler();
    private Runnable runnable;
    ParticleSystem particleSystem1,particleSystem2;
    private int counter=0;
    private static final String FORMAT = "%02d:%02d:%02d";

    int seconds , minutes;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        hitMe = view.findViewById(R.id.hit_me);
        score = view.findViewById(R.id.score);
        hitMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++;
                score.setText(""+counter);
            }
        });
        reverseTimer(10);
        particleSystem1 = new ParticleSystem(getActivity(), 80, R.drawable.confeti3, 10000);
        particleSystem2 = new ParticleSystem(getActivity(), 80, R.drawable.confeti2, 10000);
        initUI();
//        countDownStart();
        return view;
    }

    private void initUI() {
        linear_layout_1 = view.findViewById(R.id.linear_layout_1);
        linear_layout_2 = view.findViewById(R.id.linear_layout_2);
//        tv_days =  view.findViewById(R.id.tv_days);
//        tv_hour =  view.findViewById(R.id.tv_hour);
//        tv_minute =  view.findViewById(R.id.tv_minute);
        tv_second =  view.findViewById(R.id.tv_second_title);
    }

    private void countDownStart() {
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    handler.postDelayed(this, 1000);
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                    Date event_date = dateFormat.parse(EVENT_DATE_TIME);
                    Date current_date = new Date();
                    if (!current_date.after(event_date)) {
                        long diff = event_date.getTime() - current_date.getTime();
                        long Days = diff / (24 * 60 * 60 * 1000);
                        long Hours = diff / (60 * 60 * 1000) % 24;
                        long Minutes = diff / (60 * 1000) % 60;
                        long Seconds = diff / 1000 % 60;
                        //
//                        tv_days.setText(String.format("%02d", Days));
//                        tv_hour.setText(String.format("%02d", Hours));
//                        tv_minute.setText(String.format("%02d", Minutes));
                        tv_second.setText(String.format("%02d", Seconds));
                    } else {
                        linear_layout_1.setVisibility(View.VISIBLE);
                        linear_layout_2.setVisibility(View.GONE);
                        handler.removeCallbacks(runnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    public void onStop() {
        super.onStop();
        Log.d("onStop()","Its Stops");
        handler.removeCallbacks(runnable);
    }
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }
    public void reverseTimer(int Seconds){

        new CountDownTimer(Seconds* 1000+1000, 1000) {

            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tv_second.setText( String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));
            }

            public void onFinish() {
                particleSystem2.setSpeedModuleAndAngleRange(0f, 0.3f, 180, 180)
                        .setRotationSpeed(144)
                        .setAcceleration(0.00005f, 90)
                        .emit(view.findViewById(R.id.emiter_top_right), 8);

                particleSystem1.setSpeedModuleAndAngleRange(0f, 0.3f, 0, 0)
                        .setRotationSpeed(144)
                        .setAcceleration(0.00005f, 90)
                        .emit(view.findViewById(R.id.emiter_top_left), 8);
                new SweetAlertDialog(getActivity())
                        .setTitleText("Your Total Score")
                        .setContentText("Score: " + counter)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Game Over")
                                        .setContentText("Want to play again")
                                        .setConfirmText("Play Again")
                                        .setCancelText("Exit")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                new ParticleSystem(getActivity(), 80, R.drawable.confeti2, 10)
                                                        .setSpeedRange(0.2f, 0.5f)
                                                        .oneShot(getView(), 80);
                                                sDialog.dismissWithAnimation();
                                                particleSystem2.cancel();
                                                particleSystem2.stopEmitting();
                                                particleSystem1.stopEmitting();
                                                particleSystem1.cancel();
                                                reverseTimer(60);
                                                counter = 0;
                                                tv_second.setText(""+counter);

                                            }
                                        })
                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                getActivity().finishAffinity();
                                            }
                                        })
                                        .show();
                            }
                        })
                        .show();

            }
        }.start();
    }

}
