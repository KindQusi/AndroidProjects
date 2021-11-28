package com.mkuskowski.simplecalc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView input;
    private ArrayAdapter historyAdapter;
    private ListView historyView;

    private boolean canUseOperator;
    private boolean addedSecondValue;

    private ArrayList<String> arrayList;

    private double lastResult = 0f;
    private double firstValue = 0f;
    private double secondValue = 0f;

    private String operation = "";

    public static String dialogID = "WelcomeDialog";


    Map<String,Integer> operationMap = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = (TextView) findViewById(R.id.inputText);
        //Setting up history
        arrayList = new ArrayList<>();
        historyView = (ListView) findViewById(R.id.listView);
        historyAdapter = new ArrayAdapter<String>(this, R.layout.custom_textview,arrayList);
        historyView.setAdapter(historyAdapter);

        input.setText("");

        canUseOperator = false;
        addedSecondValue = false;

        setUpMap();

        WelcomeDialog focusDialog = new WelcomeDialog();
        focusDialog.show(getSupportFragmentManager(),dialogID);

        Log.println(Log.INFO,"@@@DEMOAPPLOG1","App Created! ");
    }

    public void setUpMap()
    {
        //Two args
        operationMap.put("Addition",1);
        operationMap.put("Subtraction",2);
        operationMap.put("Multiplication",3);
        operationMap.put("Division",4);
        operationMap.put("Exponentiation",5);
        operationMap.put("Percent",6);
        //One Args
        operationMap.put("Root",7);
        operationMap.put("Logarithm",8);
        operationMap.put("Sin",9);
        operationMap.put("Cos",10);
        operationMap.put("Tg",11);
    }
    //region btnClick 0-9
    public void btn_Click_0(View v)
    {
        if(!input.getText().equals("0"))
            input.setText(input.getText() + "0");
        if(operation != "")
            addedSecondValue = true;
    }
    public void btn_Click_1(View v)
    {
        input.setText(input.getText() + "1");
        canUseOperator = true;
        if(operation != "")
            addedSecondValue = true;
    }
    public void btn_Click_2(View v)
    {
        input.setText(input.getText() + "2");
        canUseOperator = true;
        if(operation != "")
            addedSecondValue = true;
    }
    public void btn_Click_3(View v)
    {
        input.setText(input.getText() + "3");
        canUseOperator = true;
        if(operation != "")
            addedSecondValue = true;
    }
    public void btn_Click_4(View v)
    {
        input.setText(input.getText() + "4");
        canUseOperator = true;
        if(operation != "")
            addedSecondValue = true;
    }
    public void btn_Click_5(View v)
    {
        input.setText(input.getText() + "5");
        canUseOperator = true;
        if(operation != "")
            addedSecondValue = true;
    }
    public void btn_Click_6(View v)
    {
        input.setText(input.getText() + "6");
        canUseOperator = true;
        if(operation != "")
            addedSecondValue = true;
    }
    public void btn_Click_7(View v)
    {
        input.setText(input.getText() + "7");
        canUseOperator = true;
        if(operation != "")
            addedSecondValue = true;
    }
    public void btn_Click_8(View v)
    {
        input.setText(input.getText() + "8");
        canUseOperator = true;
        if(operation != "")
            addedSecondValue = true;
    }
    public void btn_Click_9(View v)
    {
        input.setText(input.getText() + "9");
        canUseOperator = true;
        if(operation != "")
            addedSecondValue = true;
    }
    //endregion

    //region btnClick Two Arguments Operations
    public void btn_Click_Addition(View v)
    {
        if(canUseOperator)
        {
            if(operation == "")
            {
                Log.println(Log.INFO,"@@@DEMOAPPLOG1","Adding while no operator");
            }
            else
            {
                Log.println(Log.INFO,"@@@DEMOAPPLOG1","Adding with operator");
                btn_Click_SumUp(v);
            }
            input.setText(input.getText() + " + ");
            operation ="Addition";
        }
        canUseOperator = false;
    }
    public void btn_Click_Subtraction(View v)
    {
        if(canUseOperator)
        {
            if(operation == "")
            {
                Log.println(Log.INFO,"@@@DEMOAPPLOG1","Adding while no operator");
            }
            else
            {
                Log.println(Log.INFO,"@@@DEMOAPPLOG1","Adding with operator");
                btn_Click_SumUp(v);
            }
            input.setText(input.getText() + " - ");
            operation ="Subtraction";
        }
        canUseOperator = false;
    }
    public void btn_Click_Multiplication(View v)
    {
        if(canUseOperator)
        {
            if(operation == "")
            {
                Log.println(Log.INFO,"@@@DEMOAPPLOG1","Adding while no operator");
            }
            else
            {
                Log.println(Log.INFO,"@@@DEMOAPPLOG1","Adding with operator");
                btn_Click_SumUp(v);
            }
            input.setText(input.getText() + " × ");
            operation ="Multiplication";
        }
        canUseOperator = false;
    }
    public void btn_Click_Division(View v)
    {
        if(canUseOperator)
        {
            if(operation == "")
            {
                Log.println(Log.INFO,"@@@DEMOAPPLOG1","Adding while no operator");
            }
            else
            {
                Log.println(Log.INFO,"@@@DEMOAPPLOG1","Adding with operator");
                btn_Click_SumUp(v);
            }
            input.setText(input.getText() + " ÷ ");
            operation ="Division";
        }
        canUseOperator = false;
    }

    public void btn_Click_Exponentiation(View v)
    {
        if(canUseOperator)
        {
            if(operation == "")
            {
                Log.println(Log.INFO,"@@@DEMOAPPLOG1","Adding while no operator");
            }
            else
            {
                Log.println(Log.INFO,"@@@DEMOAPPLOG1","Adding with operator");
                btn_Click_SumUp(v);
            }
            input.setText(input.getText() + " ^ ");
            operation ="Exponentiation";
        }
        canUseOperator = false;
    }
    public void btn_Click_Percent(View v)
    {
        notifyUser("Not Implemented");
        /*
        if(canUseOperator)
        {
            if(operation == "")
            {
                Log.println(Log.INFO,"@@@DEMOAPPLOG1","Adding while no operator");
            }
            else
            {
                Log.println(Log.INFO,"@@@DEMOAPPLOG1","Adding with operator");
                btn_Click_SumUp(v);
            }
            input.setText(input.getText() + " % ");
            operation ="Exponentiation";
        }
        canUseOperator = false;
        */

    }
    //endregion

    //region One Argument Operators
    public void btn_Click_Root(View v)
    {
        if(canUseOperator)
        {
            Log.println(Log.INFO,"@@@DEMOAPPLOG1","Using Root Operator");
            try
            {
                String s = input.getText().toString();
                addToArray( "Root  " + s );
                historyAdapter.notifyDataSetChanged();
                input.setText(Double.toString(Math.sqrt(Double.parseDouble(s))));
            }
            catch (Exception e)
            {
                input.setText(0);
            }
        }
    }
    public void btn_Click_Logarithm(View v)
    {
        if(canUseOperator)
        {
            Log.println(Log.INFO,"@@@DEMOAPPLOG1","Using Log Operator");
            try
            {
                String s = input.getText().toString();
                addToArray( "Log " + s );
                historyAdapter.notifyDataSetChanged();
                input.setText(Double.toString(Math.log(Double.parseDouble(s))));
            }
            catch (Exception e)
            {
                input.setText(0);
            }
        }
    }
    public void btn_Click_Sin(View v)
    {
        if(canUseOperator)
        {
            Log.println(Log.INFO,"@@@DEMOAPPLOG1","Using Sin Operator");
            try
            {
                String s = input.getText().toString();
                addToArray( "Sin " + s );
                historyAdapter.notifyDataSetChanged();
                input.setText(Double.toString(Math.sin(Double.parseDouble(s))));
            }
            catch (Exception e)
            {
                input.setText(0);
            }
        }
    }
    public void btn_Click_Cos(View v)
    {
        if(canUseOperator)
        {
            Log.println(Log.INFO,"@@@DEMOAPPLOG1","Using Cos Operator");
            try
            {
                String s = input.getText().toString();
                addToArray( "Cos " + s );
                historyAdapter.notifyDataSetChanged();
                input.setText(Double.toString(Math.cos(Double.parseDouble(s))));
            }
            catch (Exception e)
            {
                input.setText(0);
            }
        }
    }
    public void btn_Click_Tg(View v)
    {
        if(canUseOperator)
        {
            Log.println(Log.INFO,"@@@DEMOAPPLOG1","Using Tg Operator");
            try
            {
                String s = input.getText().toString();
                addToArray( "Tg " + s );
                historyAdapter.notifyDataSetChanged();
                input.setText(Double.toString(Math.tan(Double.parseDouble(s))));
            }
            catch (Exception e)
            {
                input.setText(0);
            }
        }
    }

    //endregion

    //region Backspace/Delete
    public void btn_Click_Delete(View v)
    {
        input.setText("");
        firstValue = 0f;
        secondValue = 0f;
        lastResult = 0f;
        operation="";
        addedSecondValue = false;
        canUseOperator = false;
        arrayList.clear();
        historyAdapter.notifyDataSetChanged();
    }
    public void btn_Click_Backspace(View v)
    {
        //todo Auto skipping white spaces => while?
        if(!input.getText().equals("")) {
            char temp = input.getText().charAt(input.getText().length() - 1);
            String s = Character.toString(temp);
            Log.println(Log.INFO,"@@@DEMOAPPLOG1","Deleting :" + s);
            //todo dictionary of operators?
            if( s.equals("+") || s.equals("-") || s.equals("×") || s.equals("÷") || s.equals("^") || s.equals("%") || s.equals("."))
            {
                Log.println(Log.INFO,"@@@DEMOAPPLOG1","Deleted operator! ");
                operation = "";
                addedSecondValue = false;
                canUseOperator = true;
            }
            input.setText(input.getText().subSequence(0, input.getText().length() - 1));
        }
        else
        {
            canUseOperator = false;
        }
    }
    //endregion

    public void btn_Click_Dot(View v)
    {
        if(canUseOperator)
            input.setText(input.getText() + ".");
        canUseOperator = false;
    }

    public void btn_Click_SumUp(View v)
    {
        if(operation != "" && addedSecondValue)
        {
            int task = operationMap.get(operation);
            Log.println(Log.INFO,"@@@DEMOAPPLOG1","Trying to perform " + operation +  " id: " + task);
            getValues();
            //Two arg operations
            if ( task < 7 )
            {

                Log.println(Log.INFO,"@@@DEMOAPPLOG1","First Value = " + Double.toString(firstValue));
                Log.println(Log.INFO,"@@@DEMOAPPLOG1","Second Value = " + Double.toString(secondValue));
                switch (task)
                {
                    case 1:
                    {
                        lastResult = firstValue + secondValue;
                        break;
                    }
                    case 2:
                    {
                        lastResult = firstValue - secondValue;
                        break;
                    }
                    case 3:
                    {
                        lastResult = firstValue * secondValue;
                        break;
                    }
                    case 4:
                    {
                        if ( firstValue != 0 && secondValue != 0)
                            lastResult = firstValue / secondValue;
                        else
                            lastResult = 0;
                        break;
                    }
                    case 5:
                    {
                        //Potęga
                        lastResult = Math.pow(firstValue,secondValue);
                        break;
                    }
                    case 6:
                    {
                        //todo Procenty
                        lastResult = firstValue - (secondValue * firstValue) / 100;
                        break;
                    }
                }

            }
            //Adding to history
            addToArray(input.getText().toString());
            //Checking if we have int there
            int rounded = (int) Math.round(lastResult);
            if( rounded - lastResult == 0)
            {
                input.setText( Integer.toString(rounded) );
            }
            else
            {
                input.setText( Double.toString(lastResult) );
            }
            //Reset
            Reset();
        }
    }

    public void getValues()
    {
        Log.println(Log.INFO,"@@@DEMOAPPLOG1","Trying to get values in getSecondValue()");
        String inputText = input.getText().toString();
        int op = operationMap.get(operation);
        switch (op)
        {
            case 1:
            {
                String taskArray[] = inputText.split("\\+");
                firstValue = Double.parseDouble(taskArray[0]);
                secondValue = Double.parseDouble(taskArray[1]);
                break;
            }
            case 2:
            {
                String taskArray[] = inputText.split("-");
                firstValue = Double.parseDouble(taskArray[0]);
                secondValue = Double.parseDouble(taskArray[1]);
                break;
            }
            case 3:
            {
                String taskArray[] = inputText.split("×");
                firstValue = Double.parseDouble(taskArray[0]);
                secondValue = Double.parseDouble(taskArray[1]);
                break;
            }
            case 4:
            {
                String taskArray[] = inputText.split("÷");
                firstValue = Double.parseDouble(taskArray[0]);
                secondValue = Double.parseDouble(taskArray[1]);
                break;
            }
            case 5:
            {
                String taskArray[] = inputText.split("\\^");
                firstValue = Double.parseDouble(taskArray[0]);
                secondValue = Double.parseDouble(taskArray[1]);
                break;
            }
            case 6:
            {
                String taskArray[] = inputText.split("%");
                firstValue = Double.parseDouble(taskArray[0]);
                secondValue = Double.parseDouble(taskArray[1]);
                break;
            }
        }
    }


    private void notifyUser(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
    private void Reset()
    {
        firstValue = 0f;
        secondValue = 0f;
        lastResult = 0f;
        addedSecondValue = false;
        canUseOperator = true;
        operation = "";
    }

    private void addToArray(String var)
    {
        if (arrayList.size() > 9)
        {
            arrayList.remove(0);
        }
        arrayList.add(var);
        historyAdapter.notifyDataSetChanged();
    }
}