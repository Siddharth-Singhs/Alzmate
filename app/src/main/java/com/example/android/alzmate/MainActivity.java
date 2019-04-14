package com.example.android.alzmate;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.Manifest;

import com.example.android.alzmate.Model.CameraFragment;
import com.example.android.alzmate.Model.ChatFragment;
import com.example.android.alzmate.Model.DiaryFragment;
import com.example.android.alzmate.Model.Fragment_diary_create;
import com.example.android.alzmate.Model.HomeFragment;
import com.example.android.alzmate.Model.PeopleFragment;

import java.lang.reflect.Field;


public class MainActivity extends AppCompatActivity implements CallBackInterface,CallbackInterfaceforDiaryCreate{
    private BottomNavigationView mNavView;
    private FrameLayout mMainFrame;
    private HomeFragment homeFragment;
    private PeopleFragment peopleFragment;
    private CameraFragment cameraFragment;
    private DiaryFragment diaryFragment;
    private ChatFragment chatFragment;
    private Fragment_diary_create fragment_diary_create;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private LocationManager locationManager;
    private String provider;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavView = (BottomNavigationView) findViewById(R.id.main_nav);
        disableShiftMode((mNavView));
        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        homeFragment = new HomeFragment();
        peopleFragment = new PeopleFragment();
        cameraFragment = new CameraFragment();
        chatFragment = new ChatFragment();

        setFragement(homeFragment);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        mNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        setFragement(homeFragment);
                        currentFragment=homeFragment;
                        return true;
                    case R.id.action_people:
                        setFragement(peopleFragment);
                        currentFragment=peopleFragment;
                        return true;
                    case R.id.action_camera:
                        setFragement(cameraFragment);
                        currentFragment=cameraFragment;
                        return true;
                    case R.id.action_diary:
                        setFragementforDiaryList();
                        currentFragment=diaryFragment;
                        return true;
                    case R.id.action_chat:
                        setFragement(chatFragment);
                        currentFragment=chatFragment;
                        return true;
                    default:
                        return false;
                }
            }
        });

    }

    private void setFragement(Fragment fragement) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragement);
        fragmentTransaction.commit();

    }
    private void setFragementforDiaryList() {
        diaryFragment=new DiaryFragment();
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        diaryFragment.setCallBackInterface(this);
        fragmentTransaction.replace(R.id.main_frame,diaryFragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();

    }
    private void setFragementforDiaryCreate() {
        fragment_diary_create = new Fragment_diary_create();
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragment_diary_create.setCallBackInterface(this);
        fragmentTransaction.replace(R.id.main_frame,fragment_diary_create);
        //fragmentTransaction.addToBackStack(null);
        //FragmentManager fragmentManager = getSupportFragmentManager();
        // this will clear the back stack and displays no animation on the screen
        //fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction.commit();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission Granted
                //Do your work here
                //Perform operations here only which requires permission
            }
        }
    }

    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            if (menuView.getChildCount() < 6) {
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                    //noinspection RestrictedApi
                    item.setShifting(false);
                    //item.setShiftingMode(false);
                    // set once again checked value, so view will be updated
                    //noinspection RestrictedApi
                    item.setChecked(item.getItemData().isChecked());
                }
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }
    @Override
    public void callBackMethod() {
        Toast.makeText(this,"clicked for CallbackDiary Fragment",Toast.LENGTH_SHORT).show();
        setFragementforDiaryCreate();
    }

    @Override
    public void callbackmethodforDiaryCreate() {
        Toast.makeText(this,"clicked for Callbackfor Diary Create",Toast.LENGTH_SHORT).show();
        setFragementforDiaryList();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

