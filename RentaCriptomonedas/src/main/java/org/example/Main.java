package org.example;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {

        final char SEPARATOR=',';
        final char QUOTE='"';
        final String carpetaRenta = "C:/Users/MiUser/Downloads/";
        String binanceTransactions = carpetaRenta + "Transactions_2024_03_19_22_30.csv";

        CSVReader reader = null;
        Map<String, List<Double>> currencyRewardsMap = new HashMap<>();
        try {
            reader = new CSVReader(new FileReader(binanceTransactions),SEPARATOR,QUOTE);
            String[] nextLine;

            // Solo interesan los rewards porque:
            // 1. Los trade se reflejan en otro fichero
            // 2. Los SEND y RECEIVE son entre mis propias cuentas y no hay ganancias
            // 3. Se unifican por moneda porque solo hay 20 casillas para esto en la Renta
            int counter = 0;
            while ((nextLine = reader.readNext()) != null) {
                if (counter == 0) {
                    System.out.println(Arrays.toString(nextLine));
                    System.out.println("Elements per line: " + nextLine.length);
                    System.out.println("Received Currency: " + nextLine[8]);
                }
                if (Arrays.stream(nextLine).anyMatch("Reward"::equalsIgnoreCase)) {
                    //System.out.println("Cryptocurrency: " + nextLine[8]);
                    currencyRewardsMap.putIfAbsent(nextLine[8], new ArrayList<Double>());
                    currencyRewardsMap.get(nextLine[8]).add(Double.valueOf(nextLine[7]));
                } else {
                    // Print lines corresponding to swaps
                    System.out.println(Arrays.toString(nextLine));
                }
                //if (counter > 5) break;
                counter++;
            }
            System.out.println("Número de Lineas: " + counter);
            System.out.println("currencyRewardsMap: " + currencyRewardsMap.toString());

        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        } finally {
            if (null != reader) {
                reader.close();
            }
        }

        // Precios maximos segun investing.com
        // MATIC = 1.1354   o 1.4641 (maximo)
        // KAVA = 1.3506 maximo o 1 (media)   (desde el 23/05/2023)
        // BNB = 320 maximo
        // SHIB = 0,00001457 maximo o
        // USDT = 0,95 euros (maximo)         (desde el 23/09/2023)
        Map<String, Double> currencyValuesMap = new HashMap<>();
        currencyValuesMap.put("MATIC", 1.4641);
        currencyValuesMap.put("KAVA", 1.3506);
        currencyValuesMap.put("BNB", 320.00);
        currencyValuesMap.put("SHIB", 0.00001457);
        currencyValuesMap.put("USDT", 0.95);

        
        // Sumamos los valores para cada moneda y acumulamos los euros
        double totalStakingEuros = 0.0;
        for(Map.Entry<String, List<Double>> entrada:currencyRewardsMap.entrySet()){
            String currency = entrada.getKey();
            System.out.print(currency + " ");
            System.out.println(entrada.getValue());
            List<Double> valuesList = entrada.getValue();
            Double sum = valuesList.stream().reduce((x, y) -> x + y).get();
            System.out.println("Sumatorio: " + sum + " " + currency);
            double currencyValue = currencyValuesMap.get(currency);
            System.out.println("currencyValue: " + currencyValue + " EUROS");
            double estimatedEuros = sum * currencyValue;
            System.out.println("estimatedEuros: " + estimatedEuros + " EUROS");
            totalStakingEuros += estimatedEuros;
        }
        System.out.println("totalStakingEuros: " + totalStakingEuros + " EUROS");
    }

}