package it.isec.ami.mysmartapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Random;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public class MainActivity extends AppCompatActivity {
    DataSource ds;
    Instances insts;
    RandomForest scheme;
    TextView tv;
    Evaluation eval;
    EditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        et = findViewById(R.id.editTextTextPersonName);
        try {
            ds = new DataSource(getAssets().open("pasture.arff"));
            insts = ds.getDataSet();
            // setting class attribute if the data format does not provide this information
            // For example, the XRFF format saves the class attribute information as well
            if (insts.classIndex() == -1)
                insts.setClassIndex(insts.numAttributes() - 1);
            // create new instance of scheme
            scheme = new weka.classifiers.trees.RandomForest();
            // set options
            scheme.setOptions(weka.core.Utils.splitOptions("-I 100 -num-slots 1 -K 0 -S 1"));
            scheme.buildClassifier(insts);
            eval = new Evaluation(insts);
            eval.crossValidateModel(scheme, insts, 10, new Random(1));

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"An exception ocurred OnCreate()!", Toast.LENGTH_LONG).show();
        }


    }
    public void wekaTrain(View view){
        try {
        tv.setText(eval.toSummaryString("\nResults\n======\n", false));
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"An exception ocurred()!", Toast.LENGTH_LONG).show();
        }
    }
    public void wekaClassify(View view){
        StringBuilder arffString = new StringBuilder();
        arffString.append("@relation pasture-production\n\n@attribute fertiliser {LL,LN,HN,HH}\n@attribute slope numeric\n");
        arffString.append("@attribute aspect-dev-NW numeric\n@attribute OlsenP numeric\n@attribute MinN numeric\n@attribute TS numeric\n@attribute Ca-Mg numeric\n@attribute LOM numeric\n");
        arffString.append("@attribute NFIX-mean numeric\n@attribute Eworms-main-3 numeric\n@attribute Eworms-No-species numeric\n@attribute KUnSat numeric\n@attribute OM numeric\n");
        arffString.append("@attribute Air-Perm numeric\n@attribute Porosity numeric\n@attribute HFRG-pct-mean numeric\n@attribute legume-yield numeric\n@attribute OSPP-pct-mean numeric\n@attribute Jan-Mar-mean-TDR numeric\n");
        arffString.append("@attribute Annual-Mean-Runoff numeric\n@attribute root-surface-area numeric\n@attribute Leaf-P numeric\n@attribute pasture-prod-class {LO,MED,HI}");
        arffString.append("\n\n@data\n" + et.getText() +  ",?\n");
        try {
            Instances unlabeledData = new Instances(
                new BufferedReader(new StringReader(arffString.toString())));
            unlabeledData.setClassIndex(insts.numAttributes() - 1);
            double pred = scheme.classifyInstance(unlabeledData.instance(0));
            //get the name of the class value
            String prediction=insts.classAttribute().value((int)pred);
            tv.setText("The predicted value of instance "+prediction);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"An exception ocurred()!", Toast.LENGTH_LONG).show();
        }
    }

}