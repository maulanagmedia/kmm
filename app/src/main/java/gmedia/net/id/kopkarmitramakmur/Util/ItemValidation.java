package gmedia.net.id.kopkarmitramakmur.Util;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Shin on 3/1/2017.
 */

public class ItemValidation {

    private final String TAG = "VALIDATION";

    public String ChangeToRupiahFormat(String value){

        Float number = Float.valueOf(0);

        try {
            number = Float.parseFloat(value);
        }catch (Exception e){
            e.printStackTrace();
        }

        NumberFormat format = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols symbols = ((DecimalFormat) format).getDecimalFormatSymbols();

        symbols.setCurrencySymbol("Rp ");
        ((DecimalFormat) format).setDecimalFormatSymbols(symbols);
        format.setMaximumFractionDigits(0);

        String hasil = String.valueOf(format.format(number));

        /*String stringConvert = "0";
        try {
            stringConvert = format.format(1000);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }


        if(!stringConvert.contains(",")){
            hasil += ",00";
        }*/

        return hasil;
    }

    public String ChangeToCurrencyFormat(String value){

        Float number = Float.valueOf(0);

        try {
            number = Float.parseFloat(value);
        }catch (Exception e){
            e.printStackTrace();
        }

        DecimalFormat format = new DecimalFormat();
        String hasil = "0";
        try {
            hasil = String.valueOf(format.format(number));
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return hasil;
    }

    public String ChangeFormatDateString(String date, String formatDateFrom, String formatDateTo){

        String result = date;
        SimpleDateFormat sdf = new SimpleDateFormat(formatDateFrom);
        SimpleDateFormat sdfCustom = new SimpleDateFormat(formatDateTo);

        Date date1 = null;
        try {
            date1 = sdf.parse(date);
            return sdfCustom.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public int parseNullInteger(String s){
        int result = 0;
        if(s != null){
            try {
                result = Integer.parseInt(s);
            }catch (Exception e){
                e.printStackTrace();

            }
        }
        return result;
    }

    public float parseNullFloat(String s){
        float result = 0;
        if(s != null){
            try {
                result = Float.parseFloat(s);
            }catch (Exception e){
                e.printStackTrace();

            }
        }
        return result;
    }

    public String parseNullString(String s){
        String result = "";
        if(s != null){
            result = s;
        }
        return result;
    }

    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public TextWatcher editTextCurrency(final EditText editText) {

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                editText.removeTextChangedListener(this);
                String originalString = s.toString();

                if(originalString.contains(",")){
                    originalString = originalString.replaceAll(",", "");
                }

                if(originalString.contains(".")){
                    originalString = originalString.replaceAll("\\.", "");
                }

                DecimalFormat formatter = new DecimalFormat();
                String stringConvert = "0";
                try {
                    stringConvert = formatter.format(Double.parseDouble(originalString));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }

                editText.setText(stringConvert);
                editText.setSelection(editText.getText().length());
                editText.addTextChangedListener(this);
            }
        };
    }

    //region Datepicker
    public void datePickerEvent(final Context context, final EditText edt, final String drawablePosition, final String formatDate){

        edt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int prePosition;
                final Calendar customDate;

                switch (drawablePosition.toUpperCase()){
                    case "LEFT":
                        prePosition = 0;
                        break;
                    case "TOP":
                        prePosition = 1;
                        break;
                    case "RIGHT":
                        prePosition = 2;
                        break;
                    case "Bottom":
                        prePosition = 3;
                        break;
                    default:
                        prePosition = 2;
                }

                final int position = prePosition;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (edt.getRight() - edt.getCompoundDrawables()[position].getBounds().width())) {

                        Log.d(TAG, "onTouch: ");
                        // set format date
                        customDate = Calendar.getInstance();
                        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                                customDate.set(Calendar.YEAR,year);
                                customDate.set(Calendar.MONTH,month);
                                customDate.set(Calendar.DATE,date);

                                SimpleDateFormat sdFormat = new SimpleDateFormat(formatDate, Locale.US);
                                edt.setText(sdFormat.format(customDate.getTime()));
                            }
                        };

                        new DatePickerDialog(context,date,customDate.get(Calendar.YEAR),customDate.get(Calendar.MONTH),customDate.get(Calendar.DATE)).show();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void datePickerEventKlick(final Context context, final EditText edt, final String formatDate){

        edt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final Calendar customDate;

                edt.setError(null);

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    // set format date
                    customDate = Calendar.getInstance();
                    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                            customDate.set(Calendar.YEAR,year);
                            customDate.set(Calendar.MONTH,month);
                            customDate.set(Calendar.DATE,date);

                            SimpleDateFormat sdFormat = new SimpleDateFormat(formatDate, Locale.US);
                            edt.setText(sdFormat.format(customDate.getTime()));
                        }
                    };

                    new DatePickerDialog(context,date,customDate.get(Calendar.YEAR),customDate.get(Calendar.MONTH),customDate.get(Calendar.DATE)).show();
                    return true;
                }
                return false;
            }
        });
    }
    //endregion

    public boolean isMoreThanTheDate(EditText edt, EditText edt2, String format){

        if(edt == null || edt2 == null){
            return true;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date dateToCompare = null;
        Date dateCompare = null;

        try {
            dateToCompare = sdf.parse(edt.getText().toString());
            dateCompare = sdf.parse(edt2.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(dateToCompare.after(dateCompare) || dateToCompare.equals(dateCompare)){
            return true;
        }else{
            return false;
        }
    }
}
