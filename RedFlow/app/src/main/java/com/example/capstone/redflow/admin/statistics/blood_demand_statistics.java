package com.example.capstone.redflow.admin.statistics;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.capstone.redflow.R;
import com.example.capstone.redflow.ToolBox;
import com.example.capstone.redflow.notimportant.DemoBase;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class blood_demand_statistics extends DemoBase implements OnChartValueSelectedListener {

    private PieChart mChart;

    private Typeface tf;

    private Firebase mRootRef;
    private Query query;
    private ValueEventListener listener;

    private int A;
    private int B;
    private int O;
    private int AB;
    private int nA;
    private int nB;
    private int nO;
    private int nAB;
    private int month;
    private int year;

    private String turf;

    private TextView ap;
    private TextView am;
    private TextView op;
    private TextView om;
    private TextView bp;
    private TextView bm;
    private TextView abp;
    private TextView abm;
    private TextView datetext;

    private LinearLayout Lap;
    private LinearLayout Lam;
    private LinearLayout Lop;
    private LinearLayout Lom;
    private LinearLayout Lbp;
    private LinearLayout Lbm;
    private LinearLayout Labp;
    private LinearLayout Labm;

    private Calendar calendar;

    private ToolBox tools;

    ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.blood_demmand_statistics);

        mRootRef = new Firebase("https://redflow-22917.firebaseio.com/");

        tools = new ToolBox();

        turf = getIntent().getStringExtra("turf");

        calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);

        datetext = (TextView) findViewById(R.id.date);
        datetext.setText(tools.int2Month(month) + ", " + year);

        mChart = (PieChart) findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        mChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        mChart.setCenterText(generateCenterSpannableText());

        mChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        setData(4, 100);

        //mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);
        mChart.animateXY(1400, 1400);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);

        op = (TextView) findViewById(R.id.oplus);
        om = (TextView) findViewById(R.id.ominus);
        ap = (TextView) findViewById(R.id.aplus);
        am = (TextView) findViewById(R.id.aminus);
        bp = (TextView) findViewById(R.id.bplus);
        bm = (TextView) findViewById(R.id.bminus);
        abp = (TextView) findViewById(R.id.abplus);
        abm = (TextView) findViewById(R.id.abminus);

        Lop = (LinearLayout) findViewById(R.id.o1);
        Lom = (LinearLayout) findViewById(R.id.o2);
        Lap = (LinearLayout) findViewById(R.id.a1);
        Lam = (LinearLayout) findViewById(R.id.a2);
        Lbp = (LinearLayout) findViewById(R.id.bb1);
        Lbm = (LinearLayout) findViewById(R.id.bb2);
        Labp = (LinearLayout) findViewById(R.id.ab1);
        Labm = (LinearLayout) findViewById(R.id.ab2);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pie, menu);
        return true;
    }


    private void setData(int count, float range) {

        float mult = range;

        A = 0;
        B = 0;
        O = 0;
        AB = 0;
        nA = 0;
        nB = 0;
        nO = 0;
        nAB = 0;

        query = mRootRef.child("Request").child(turf).child(String.valueOf(month));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Integer> map = dataSnapshot.getValue(Map.class);

                A = map.get("A+");
                B = map.get("B+");
                O = map.get("O+");
                AB = map.get("AB+");
                nA = map.get("A-");
                nB = map.get("B-");
                nO = map.get("O-");
                nAB = map.get("AB-");

                if(A > 0) {
                    entries.add(new PieEntry(A, "Type A+"));
                }else{
                    Lap.setVisibility(View.GONE);
                }
                if(B > 0) {
                    entries.add(new PieEntry(B, "Type B+"));
                }else{
                    Lbp.setVisibility(View.GONE);
                }
                if(O > 0) {
                    entries.add(new PieEntry(O, "Type O+"));
                }else{
                    Lop.setVisibility(View.GONE);
                }
                if(AB > 0) {
                    entries.add(new PieEntry(AB, "Type AB+"));
                }else{
                    Labp.setVisibility(View.GONE);
                }
                if(nA > 0) {
                    entries.add(new PieEntry(nA, "Type A-"));
                }else{
                    Lam.setVisibility(View.GONE);
                }
                if(nB > 0) {
                    entries.add(new PieEntry(nB, "Type B-"));
                }else{
                    Lbm.setVisibility(View.GONE);
                }
                if(nO > 0) {
                    entries.add(new PieEntry(nO, "Type O-"));
                }else{
                    Lom.setVisibility(View.GONE);
                }
                if(nAB > 0) {
                    entries.add(new PieEntry(nAB, "Type AB-"));
                }else{
                    Labm.setVisibility(View.GONE);
                }

                PieDataSet dataSet = new PieDataSet(entries, "Demand");
                dataSet.setSliceSpace(3f);
                dataSet.setSelectionShift(5f);

                ArrayList<Integer> colors = new ArrayList<Integer>();

                for (int c : ColorTemplate.MATERIAL_COLORS)
                    colors.add(c);


                colors.add(ColorTemplate.getHoloBlue());

                dataSet.setColors(colors);
                //dataSet.setSelectionShift(0f);


                dataSet.setValueLinePart1OffsetPercentage(80.f);
                dataSet.setValueLinePart1Length(0.2f);
                dataSet.setValueLinePart2Length(0.4f);
                //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

                PieData data = new PieData(dataSet);
                data.setValueFormatter(new PercentFormatter());
                data.setValueTextSize(11f);
                data.setValueTextColor(Color.BLACK);
                data.setValueTypeface(tf);
                mChart.setData(data);

                // undo all highlights
                mChart.highlightValues(null);

                mChart.invalidate();

                op.setText(String.valueOf(O)+" bag(s)");
                om.setText(String.valueOf(nO)+" bag(s)");
                ap.setText(String.valueOf(A)+" bag(s)");
                am.setText(String.valueOf(nA)+" bag(s)");
                bp.setText(String.valueOf(B)+" bag(s)");
                bm.setText(String.valueOf(nB)+" bag(s)");
                abp.setText(String.valueOf(AB)+" bag(s)");
                abm.setText(String.valueOf(nAB)+" bag(s)");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("RedFlow\nData Analytics");
        s.setSpan(new RelativeSizeSpan(1.8f), 0, 8, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 8, s.length() - 4, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 8, s.length() - 4, 0);
        s.setSpan(new RelativeSizeSpan(.65f), 8, s.length() - 14, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length(), s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }
}
