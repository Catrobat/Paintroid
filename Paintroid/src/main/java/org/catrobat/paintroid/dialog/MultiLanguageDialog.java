/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2014 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.dialog;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;

import java.util.Locale;

public class MultiLanguageDialog extends DialogFragment implements View.OnClickListener, DialogInterface.OnClickListener {
    String LOCALE_ARABIC = "ar";
    String LOCALE_ENGLISH = "en";
    String LOCALE_GERMAN = "de";
    String LOCALE_FRENCH="fr";
    String LOCALE_ITALIAN="it";
    String LOCALE_SPANISH="es";
    String LOCALE_HUNGARIAN="hu";
    String LOCALE_JAPANESE="ja";
    String LOCALE_PERSIAN="fa";
    String LOCALE_KOREAN="ko";
    String LOCALE_NORWEGIAN="nn";
    String LOCALE_POLISH="pl";
    String LOCALE_PORTUGUESE="pt";
    String LOCALE_ROMANIAN="ro";
    String LOCALE_RUSSIAN="ru";
    String LOCALE_SLOVENIAN="sl";
    String LOCALE_SERBIAN="sr";
    String LOCALE_TURKISH="tr";
    String LOCALE_CHINESE="zh";
    String LOCALE_SWEDISH= "sv";
    String LOCALE_FARSI= "fa";
    String LOCALE_HINDI= "hi";
    String LOCALE_URDU= "ur";
    Locale mLocale;
    Intent intent;
    RadioButton choiceArabic,choiceEnglish, choiceGerman,choiceSwedish,choiceFRENCH,choiceITALIAN,choiceSPANISH,choiceHUNGARIAN,
    choiceJAPANESE,choicePOLISH,choicePERSIAN,choicePORTUGUESE,choiceTURKISH,choiceRUSSIAN,choiceURDU,choiceKorean  ;

    public MultiLanguageDialog() {
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new CustomAlertDialogBuilder(
                getActivity());
        builder.setTitle("Language Settings");
        View view = inflater.inflate(R.layout.language_settings, null);
         choiceArabic = (RadioButton) view
                .findViewById(R.id.Arabic_choice);
         choiceArabic.setOnClickListener(this);
         choiceEnglish = (RadioButton) view
                .findViewById(R.id.English_choice);
         choiceEnglish.setOnClickListener(this);
         choiceGerman = (RadioButton) view
                .findViewById(R.id.German_choice);
         choiceGerman.setOnClickListener(this);
         choiceSwedish = (RadioButton) view
                .findViewById(R.id.Swedish_choice);
         choiceSwedish.setOnClickListener(this);
        choiceFRENCH = (RadioButton) view
                .findViewById(R.id.French_choice);
        choiceFRENCH.setOnClickListener(this);
        choiceSPANISH = (RadioButton) view
                .findViewById(R.id.Spanish_choice);
        choiceSPANISH.setOnClickListener(this);
        choiceHUNGARIAN = (RadioButton) view
                .findViewById(R.id.Hungarian_choice);
        choiceHUNGARIAN.setOnClickListener(this);
        choiceITALIAN = (RadioButton) view
                .findViewById(R.id.Itanlian_choice);
        choiceITALIAN.setOnClickListener(this);
        choiceJAPANESE = (RadioButton) view
                .findViewById(R.id.Japanese_choice);
        choiceJAPANESE.setOnClickListener(this);
        choicePOLISH = (RadioButton) view
                .findViewById(R.id.Polish_choice);
        choicePOLISH.setOnClickListener(this);

        choicePORTUGUESE = (RadioButton) view
                .findViewById(R.id.Portuguese_choice);
        choicePORTUGUESE.setOnClickListener(this);

        choiceRUSSIAN= (RadioButton) view
                .findViewById(R.id.Russian_choice);
        choiceRUSSIAN.setOnClickListener(this);

        choiceURDU= (RadioButton) view
                .findViewById(R.id.Urdu_choice);
        choiceURDU.setOnClickListener(this);

        choiceTURKISH= (RadioButton) view
                .findViewById(R.id.Turkish_choice);
        choiceTURKISH.setOnClickListener(this);

        choicePERSIAN= (RadioButton) view
                .findViewById(R.id.Persian_choice);
        choicePERSIAN.setOnClickListener(this);

        choiceKorean=(RadioButton) view
                .findViewById(R.id.Korean_choice);
        choiceKorean.setOnClickListener(this);
        builder.setView(view);
        builder.setNeutralButton(R.string.done, this);
        builder.setNegativeButton(R.string.cancel, this);
        return builder.create();

    }

    public void onClick(View v) {
        Button clickedButton = ( Button ) v;
        Configuration newConfig = new Configuration();

        switch (clickedButton.getId()) {
            case R.id.Arabic_choice:
                mLocale = new Locale(LOCALE_ARABIC);
                break;
            case R.id.English_choice:
                mLocale = new Locale(LOCALE_ENGLISH);
                break;
            case R.id.German_choice:
                mLocale = new Locale(LOCALE_GERMAN);
                break;
            case R.id.French_choice:
                mLocale = new Locale(LOCALE_FRENCH);
                break;
            case R.id.Spanish_choice:
                mLocale = new Locale(LOCALE_SPANISH);
                break;
            case R.id.Hungarian_choice:
                mLocale = new Locale(LOCALE_HUNGARIAN);
                break;
            case R.id.Itanlian_choice:
                mLocale = new Locale(LOCALE_ITALIAN);
                break;
            case R.id.Japanese_choice:
                mLocale = new Locale(LOCALE_JAPANESE);
                break;
            case R.id.Portuguese_choice:
                mLocale = new Locale(LOCALE_PORTUGUESE);
                break;
            case R.id.Russian_choice:
                mLocale = new Locale(LOCALE_RUSSIAN);
                break;
            case R.id.Turkish_choice:
                mLocale = new Locale(LOCALE_TURKISH);
                break;
            case R.id.Swedish_choice:
                mLocale = new Locale(LOCALE_SWEDISH);
                break;
            case R.id.Persian_choice:
                mLocale = new Locale(LOCALE_PERSIAN);
                break;
            case R.id.Urdu_choice:
                mLocale = new Locale(LOCALE_URDU,"PK");
                break;
            case R.id.Korean_choice:
                mLocale = new Locale(LOCALE_KOREAN);
                break;

            default:
             break;
        }//end of Switch
    }

    @Override
    public void onStart() {
        super.onStart();
        choiceArabic.setChecked(true);
        mLocale = new Locale(LOCALE_ARABIC);

    }
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case AlertDialog.BUTTON_NEUTRAL:
                dismiss();
                Locale.setDefault(mLocale);
                Configuration config = new Configuration();
                config.locale = mLocale;
                getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());
                getActivity().finishAffinity();
              try {
                   Thread.sleep(900);
                   restartActivity();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
               break;
            case AlertDialog.BUTTON_NEGATIVE:
                dialog.dismiss();
        }
    }
    private void restartActivity() {
       //  getActivity().overridePendingTransition(0, 0);
         Intent myIntent = new Intent(getActivity(), MainActivity.class);
         startActivity(myIntent);
        // getActivity().overridePendingTransition(0, 0);
    }

}




