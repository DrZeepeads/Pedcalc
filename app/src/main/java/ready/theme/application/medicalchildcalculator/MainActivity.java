package ready.theme.application.medicalchildcalculator;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText mAgeEditText;
    EditText mWeightEditText;
    Integer childAge;
    Integer childWeight;
    ListView childResultsList;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize AdMob
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        // Initialize AdMob banner
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Initialize AdMob interstitial
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        // Initialize application fields
        mAgeEditText = findViewById(R.id.ageEditText);
        mWeightEditText = findViewById(R.id.weightEditText);
        childResultsList = findViewById(R.id.resultsListView);
    }

    public void clearButtonClicked(View view) {
        // Clear weight and age EditTexts
        mAgeEditText.setText("");
        mWeightEditText.setText("");

        // Delete list
        childResultsList.setAdapter(null);
    }

    // Check if user entered age as Integer
    public static boolean isInteger(String s) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), 10) < 0) return false;
        }
        return true;
    }

    // Check if age is from 1 to 12
    public static boolean isOneToTwelve(Integer i) {
        return i > 0 && i < 13;
    }

    // Show error toast
    public void displayErrorAge() {
        String errorMessage = "Please, enter age as a whole number from 1 to 12";
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    // Calculate weightGuessed
    public static Integer weightAPLS(Integer age) {
        int weightGuessed;
        if (age >= 1 && age <= 5) weightGuessed = (age * 2) + 8;
        else weightGuessed = (age * 3) + 7;
        return weightGuessed;
    }

    // Show error weight
    public void displayErrorWeight() {
        String errorMessage = "Please, enter weight as a whole number";
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    // Calculate tube size
    public String getTubeSize(Integer age) {
        String tubeSize;
        double ts;
        if (age <= 2) {
            tubeSize = "4.0 - 4.5";
            // if age/4+4 is sth.25 or sth.75 return next smaller to next larger size
        } else if (age % 4 == 3 || age % 4 == 1) {
            ts = ((((double) (age)) / 4) + 4) - 0.25;
            tubeSize = ts + " - " + (ts + 0.5);
        } else {
            ts = ((((double) (age)) / 4) + 4);
            tubeSize = Double.toString(ts);
        }
        return tubeSize;
    }

    private ArrayList<Map<String, String>> buildData() {

        // Calculate all variables
        int mDefi = childWeight * 4;
        double mAdr = round((childWeight * 0.01), 2);
        int mAmio = childWeight * 5;
        String mTubeSize = getTubeSize(childAge);
        int mBolus = childWeight * 20;
        Integer mCardiov1 = childWeight;
        int mCardiov2 = childWeight * 2;
        double mEto1 = round((childWeight * 0.2), 2);
        double mEto2 = round((childWeight * 0.3), 2);
        double mMida1 = round((childWeight * 0.1), 2);
        double mMida2 = round((childWeight * 0.2), 2);
        int mThio1 = childWeight * 3;
        int mThio2 = childWeight * 5;
        double mEsketam1 = round((childWeight * 0.5), 2);
        double mEsketam2 = round((childWeight * 1.0), 2);
        int mKetam1 = childWeight;
        int mKetam2 = childWeight * 2;
        double mMorph = round((childWeight * 0.1), 2);
        double mFenta1 = round((childWeight * 0.001), 3);
        double mFenta2 = round((childWeight * 0.003), 3);
        double mSucci = round((childWeight * 1.5), 2);
        Integer mRocu = childWeight;

        // Add all variables to list for user
        ArrayList<Map<String, String>> list = new ArrayList<>();
        list.add(putData(getResources().getString(R.string.Defibrillation) + " " + mDefi + " " + getResources().getString(R.string.Joules), getResources().getString(R.string.a1)));
        list.add(putData(getResources().getString(R.string.Adrenaline) + " " + mAdr + " " + getResources().getString(R.string.mg), getResources().getString(R.string.a2)));
        list.add(putData(getResources().getString(R.string.Amiodarone) + ": " + mAmio + " " + getResources().getString(R.string.mg), getResources().getString(R.string.a3)));
        list.add(putData(getResources().getString(R.string.TrachealTubeID) + " " + mTubeSize + " " + getResources().getString(R.string.mm), getResources().getString(R.string.a4)));
        list.add(putData(getResources().getString(R.string.IVfluidbolus) + " " + mBolus + " " + getResources().getString(R.string.ml), getResources().getString(R.string.a5)));
        list.add(putData(getResources().getString(R.string.Cardioversion) + " " + mCardiov1 + " (2nd: " + mCardiov2 + ") Joules", getResources().getString(R.string.a6)));
        list.add(putData(getResources().getString(R.string.Etomidate) + " " + mEto1 + " - " + mEto2 + " " + getResources().getString(R.string.mg), getResources().getString(R.string.a7)));
        list.add(putData(getResources().getString(R.string.Midazolam) + " " + mMida1 + " - " + mMida2 + " " + getResources().getString(R.string.mg), getResources().getString(R.string.a8)));
        list.add(putData(getResources().getString(R.string.Thiopental) + " " + mThio1 + " - " + mThio2 + " " + getResources().getString(R.string.mg), getResources().getString(R.string.a9)));
        list.add(putData(getResources().getString(R.string.Esketamin) + " " + mEsketam1 + " - " + mEsketam2 + " " + getResources().getString(R.string.mg), getResources().getString(R.string.a10)));
        list.add(putData(getResources().getString(R.string.Ketamin) + " " + mKetam1 + " - " + mKetam2 + " " + getResources().getString(R.string.mg), getResources().getString(R.string.a11)));
        list.add(putData(getResources().getString(R.string.Morphine) + " " + mMorph + " " + getResources().getString(R.string.mg), getResources().getString(R.string.a12)));
        list.add(putData(getResources().getString(R.string.Fentanyl) + " " + mFenta1 + " - " + mFenta2 + " " + getResources().getString(R.string.mg), getResources().getString(R.string.a13)));
        list.add(putData(getResources().getString(R.string.Succinylcholine) + " " + mSucci + " " + getResources().getString(R.string.mg), getResources().getString(R.string.a14)));
        list.add(putData(getResources().getString(R.string.Rocuronium) + " " + mRocu + " " + getResources().getString(R.string.mg), getResources().getString(R.string.a15)));

        return list;
    }

    // Put data to HashMap
    private HashMap<String, String> putData(String dosage, String formula) {
        HashMap<String, String> item = new HashMap<>();
        item.put("dosage", dosage);
        item.put("formula", formula);
        return item;
    }


    public void goButtonClicked(View view) {
        // Show AdMob Interstitial
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }

        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // Check if user wrote all variables correctly
        if (isInteger(mAgeEditText.getText().toString()) && isOneToTwelve(Integer.valueOf(mAgeEditText.getText().toString()))) {
            childAge = Integer.valueOf(mAgeEditText.getText().toString());

            if (mWeightEditText.getText().toString().isEmpty()) {
                childWeight = weightAPLS(childAge);
                mWeightEditText.setText(childWeight.toString());
            } else if (isInteger(mWeightEditText.getText().toString())) {
                childWeight = Integer.valueOf(mWeightEditText.getText().toString());
            } else displayErrorWeight();

            ArrayList<Map<String, String>> list = buildData();
            String[] from = {"dosage", "formula"};
            int[] to = {R.id.text1, R.id.text2};

            SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.results_list_view, from, to);
            childResultsList.setAdapter(adapter);

        } else
            displayErrorAge();
    }
}
