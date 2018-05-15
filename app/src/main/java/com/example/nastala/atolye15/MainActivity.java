package com.example.nastala.atolye15;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MAINACTIVITY";
    private boolean kucukUnluUyumu;
    private boolean buyukUnluUyumu;
    private boolean buyukUnluFlag;
    private boolean maxPointFromLetters;
    private boolean turkishLetterCheck;
    private String buyukUnluTipi = "";
    private String kucukUnluTipi = "";
    private String letterTipi = "";

    private Button btnTest;
    private EditText etText;
    private TextView tvResult;
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTest = findViewById(R.id.btnTest);
        etText = findViewById(R.id.etText);
        tvResult = findViewById(R.id.tvResult);
        tvInfo = findViewById(R.id.tvInfo);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etText.getText().toString().isEmpty())
                    checkText(etText.getText().toString());
            }
        });
    }

    private void checkText(String s) {
        if(s.isEmpty() || s.replaceAll("\\s+", "").isEmpty()){
            return;
        }

        String[] words = s.split(" ");
        int[] result = new int[words.length];
        String[] resultText = new String[words.length];
        StringBuilder finalResult = new StringBuilder();

        resultText[0] += "Başlangıç puanı 40'dır. \n";

        for (int i = 0; i < words.length; i++){
            if(!Pattern.matches("[a-zA-Z]", words[i]))
                Log.d(TAG, words[i] + " regex failed");

            resultText[i] = words[i] + " Kelime Sonucu: \n";
            result[i] = 40;

            if(words[i].endsWith("b") || words[i].endsWith("c") || words[i].endsWith("d") || words[i].endsWith("g") ||
                    words[i].endsWith("B") || words[i].endsWith("C") || words[i].endsWith("D") || words[i].endsWith("G")) {
                resultText[i] += "b,c,d ve ya g harflerinden biri ile bitiyor -20 \n";
                result[i] -= 20;
                Log.d(TAG, "BCDG ile bitiyor");
                //continue;
            }

            //Log.d(TAG, "CONTINUE FLAG");

            char[] letters = words[i].toCharArray();
            if(letters.length < 1)
                continue;

            int exactSameLetterCount = 0;
            boolean sesliExactSameLetterCheck = false;
            boolean sessizExactSameLetterCheck = false;
            boolean sesliHarfeSahipMi = false;
            boolean sessizHarfeSahipMi = false;
            boolean harfMi = true;

            if(isSesliHarf(letters[0]))
                letterTipi = "sesli";
            else
                letterTipi = "sessiz";

            buyukUnluFlag = false;
            turkishLetterCheck = false;
            maxPointFromLetters = false;
            kucukUnluUyumu = true;
            buyukUnluUyumu = true;
            for(int j = 0; j < letters.length; j++){
                if(!isHarf(letters[j])){
                    resultText[i] = words[i] + " Kelime Sonucu: \n";
                    resultText[i] += "Kelime sayı ya da özel harf içeriyor. \n";
                    result[i] = 0;
                    resultText[i] += "Sonuç: %" + result[i] + " Türkçe'dir \n\n\n";
                    finalResult.append(resultText[i]);
                    harfMi = false;
                    break;
                }

                if(isSesliHarf(letters[j])) {
                    sesliHarfeSahipMi = true;

                    if(!buyukUnluFlag) {
                        if(isKalinUnlu(letters[j]))
                            buyukUnluTipi = "kalin";
                        else if(isInceUnlu(letters[j]))
                            buyukUnluTipi = "ince";
                        else
                            Log.d(TAG, "Beklenmedik tip");

                        if(isDuz(letters[j]))
                            kucukUnluTipi = "duz";
                        else if(isYuvarlak(letters[j]))
                            kucukUnluTipi = "yuvarlak";
                        else
                            Log.d(TAG, "Kucuk ünlü beklenmedik tip");

                        buyukUnluFlag = true;
                    }

                    if(!kucukUnluUyumuKontrol(letters[j])) {
                        kucukUnluUyumu = false;
                        Log.d(TAG, "Küçük ünlü uyumu false");
                    }

                    if(!buyukUnluUyumuKontrol(letters[j])){
                        buyukUnluUyumu = false;
                        Log.d(TAG, "Büyük ünlü uyumu false");
                    }
                }
                else
                    sessizHarfeSahipMi = true;

                if(isSesliHarf(letters[j]) && letterTipi.equals("sesli"))
                    exactSameLetterCount++;
                else if(!isSesliHarf(letters[j]) && letterTipi.equals("sessiz"))
                    exactSameLetterCount++;
                else {
                    if(isSesliHarf(letters[j]))
                        letterTipi = "sesli";
                    else
                        letterTipi = "sessiz";

                    exactSameLetterCount = 1;
                }

                Log.d(TAG, "Same letter count: " + exactSameLetterCount + " letter tipi: " + letterTipi);

                if(exactSameLetterCount > 1 && letterTipi.equals("sesli") && !sesliExactSameLetterCheck){
                    Log.d(TAG, "2 ya da daha fazla ard arda sesli harf var");
                    resultText[i] += "2 ya da daha fazla ard arda sesli harf var -20 \n";
                    result[i] -= 20;
                    sesliExactSameLetterCheck = true;
                }
                else if(exactSameLetterCount > 2 && letterTipi.equals("sessiz") && !sessizExactSameLetterCheck){
                    Log.d(TAG, "3 ya da daha fazla ard arda sessiz harf var");
                    resultText[i] += "3 ya da daha fazla ard arda sessiz harf var -20 \n";
                    result[i] -= 20;
                    sessizExactSameLetterCheck = true;
                }

                if(!maxPointFromLetters) {
                    int temp = isTurkishLetter(letters[j]);

                    if(turkishLetterCheck) {
                        if(temp == 20) {
                            result[i] += 10;
                        }
                    }
                    else if(temp != 0){
                        result[i] += isTurkishLetter(letters[j]);
                        turkishLetterCheck = true;
                    }
                    else {
                        Log.d(TAG, "isTurkishLetter " + letters[j] + " false");
                    }
                }
            }

            if(!harfMi)
                continue;

           if(!sesliHarfeSahipMi || !sessizHarfeSahipMi){
                resultText[i] = words[i] + " Kelime Sonucu: \n";
                resultText[i] += "Hiç sesli ya da sessiz harf içermiyor. \n";
                result[i] = 0;
                resultText[i] += "Sonuç: %" + result[i] + " Türkçe'dir \n\n\n";
                finalResult.append(resultText[i]);
                continue;
            }

            if(turkishLetterCheck && maxPointFromLetters)
                resultText[i] += "Sadece Türkçe'de bulunan harf içeriyor +20 \n";
            else if(turkishLetterCheck && !maxPointFromLetters)
                resultText[i] += "Sadece Türkçe'de bulunmayan harf içeriyor +10 \n";

            if(kucukUnluUyumu && sesliHarfeSahipMi) {
                resultText[i] += "Küçük Ünlü Uyumu'na uyuyor +20 \n";
                result[i] += 20;
            }

            if(buyukUnluUyumu && sesliHarfeSahipMi) {
                resultText[i] += "Büyük Ünlü Uyumu'na uyuyor +20 \n";
                result[i] += 20;
            }

            Log.d(TAG, "Result " + i + ": " + result[i] + " " + resultText[i]);

            /*if(result[i] > 100)
                result[i] = 100;
            if(result[i] < 0)
                result[i] = 0;*/

            resultText[i] += "Sonuç: %" + result[i] + " Türkçe'dir";
            resultText[i] += "\n\n\n";
            finalResult.append(resultText[i]);
        }

        tvInfo.setVisibility(View.VISIBLE);
        tvResult.setText(finalResult);
    }

    private boolean isKalinUnlu(char c){
        return String.valueOf(c).matches("[aıouAIOU]");
    }

    private boolean isInceUnlu(char c){
        return String.valueOf(c).matches("[eiöüEİÖÜ]");
    }

    private boolean isSesliHarf(char c){
        return String.valueOf(c).matches("[aeıioöuüAEIİOÖUÜ]");
    }

    private boolean isYuvarlak(char c){
        return String.valueOf(c).matches("[oöuüOÖUÜ]");
    }

    private boolean isDuz(char c){
        return String.valueOf(c).matches("[aeıiAEIİ]");
    }

    private boolean isYuvarlakDar(char c){
        return String.valueOf(c).matches("[uüUÜ]");
    }

    private boolean isDuzGenis(char c){
        return String.valueOf(c).matches("[aeAE]");
    }

    private boolean isHarf(char c){
        return String.valueOf(c).matches("[a-zA-ZçÇİığĞşŞöÖüÜ]");
    }

    private boolean buyukUnluUyumuKontrol(char c) {
        Log.d(TAG, "Büyük ünlü uyumu flag " + c);

        if(buyukUnluTipi.equals("kalin")){
            return isKalinUnlu(c);
        }
        else if(buyukUnluTipi.equals("ince"))
            return isInceUnlu(c);
        else
            Log.d(TAG, "Büyükünlütipi hata");

        Log.d(TAG, "Büyük ünlü uyumu true");
        return  false;
    }

    private boolean kucukUnluUyumuKontrol(char c){
        Log.d(TAG, "Küçük ünlü uyumu flag " + c);

        if(kucukUnluTipi.equals("duz"))
            return isDuz(c);
        else if(kucukUnluTipi.equals("yuvarlak")){
            return isYuvarlakDar(c) || isDuzGenis(c);
        }
        else
            Log.d(TAG, "kucuk ünlü uyumu beklenmedik tip");

        return false;
    }

    private int isTurkishLetter(char c){
        int point = 0;

        switch(c){
            case 'Ö':
                point = 10;
                break;
            case 'ö':
                point = 10;
                break;
            case 'Ü':
                point = 10;
                break;
            case 'ü':
                point = 10;
                break;
            case 'ç':
                point = 10;
                break;
            case 'Ç':
                point = 10;
                break;
            case 'ı':
                point = 20;
                break;
            case 'ğ':
                point = 20;
                break;
            case 'İ':
                point = 20;
                break;
            case 'Ğ':
                point = 20;
                break;
            case 'ş':
                point = 20;
                break;
            case 'Ş':
                point = 20;
                break;
        }

        if(point == 20)
            maxPointFromLetters = true;

        Log.d(TAG, "isTurkishLetter point: " + point + " letter: " + c);

        return point;
    }
}
