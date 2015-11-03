package com.tiksem.media.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * User: stikhonenko
 * Date: 3/7/13
 * Time: 4:56 PM
 */
public final class Countries {
    private static Map<String, String[]> countries = new TreeMap<>();
    private static List<String> countriesNamesList;

    static {
        putCountry("Ukraine", "Ukraine","ukrainian");
        putCountry("Russia", "Russia","russian");
        putCountry("Afghanistan", "Afghanistan");
        putCountry("Albania", "Albania");
        putCountry("Algeria", "Algeria");
        putCountry("Andorra", "Andorra");
        putCountry("Angola", "Angola");
        putCountry("Antigua and Barbuda", "Antigua and Barbuda");
        putCountry("Argentina", "Argentina");
        putCountry("Armenia", "Armenia");
        putCountry("Australia", "Australia");
        putCountry("Austria", "Austria");
        putCountry("Azerbaijan", "Azerbaijan");
        putCountry("Bahamas", "Bahamas");
        putCountry("Bahrain", "Bahrain");
        putCountry("Bangladesh", "Bangladesh");
        putCountry("Barbados", "Barbados");
        putCountry("Belarus", "Belarus");
        putCountry("Belgium", "Belgium");
        putCountry("Belize", "Belize");
        putCountry("Benin", "Benin");
        putCountry("Bhutan", "Bhutan");
        putCountry("Bolivia", "Bolivia");
        putCountry("Bosnia and Herzegovina", "Bosnia and Herzegovina");
        putCountry("Botswana", "Botswana");
        putCountry("Brazil", "Brazil");
        putCountry("Brunei", "Brunei");
        putCountry("Bulgaria", "Bulgaria");
        putCountry("Burkina Faso", "Burkina Faso");
        putCountry("Burma", "Burma");
        putCountry("Burundi", "Burundi");
        putCountry("Cambodia", "Cambodia");
        putCountry("Cameroon", "Cameroon");
        putCountry("Canada", "Canada");
        putCountry("Cape Verde", "Cape Verde");
        putCountry("Central African Republic", "Central African Republic");
        putCountry("Chad", "Chad");
        putCountry("Chile", "Chile");
        putCountry("China", "China");
        putCountry("Colombia", "Colombia");
        putCountry("Comoros", "Comoros");
        putCountry("Congo", "Congo","Democratic Republic of the Congo");
        putCountry("Costa Rica", "Costa Rica");
        putCountry("Croatia", "Croatia");
        putCountry("Cuba", "Cuba");
        putCountry("Cyprus", "Cyprus");
        putCountry("Czech Republic", "Czech Republic");
        putCountry("Korea", "Korea");
        putCountry("North Korea", "North Korea");
        putCountry("South Korea", "South Korea");
        putCountry("Denmark", "Denmark");
        putCountry("Djibouti", "Djibouti");
        putCountry("Dominica", "Dominica");
        putCountry("Dominican Republic", "Dominican Republic");
        putCountry("East Timor", "East Timor");
        putCountry("Ecuador", "Ecuador");
        putCountry("Egypt", "Egypt");
        putCountry("El Salvador", "El Salvador");
        putCountry("Equatorial Guinea", "Equatorial Guinea");
        putCountry("Eritrea", "Eritrea");
        putCountry("Estonia", "Estonia");
        putCountry("Ethiopia", "Ethiopia");
        putCountry("Fiji", "Fiji");
        putCountry("Finland", "Finland");
        putCountry("France", "France");
        putCountry("Gabon", "Gabon");
        putCountry("Gambia", "Gambia");
        putCountry("Georgia", "Georgia");
        putCountry("Germany", "Germany");
        putCountry("Greece", "Greece");
        putCountry("Grenada", "Grenada");
        putCountry("Guatemala", "Guatemala");
        putCountry("Guinea", "Guinea");
        putCountry("Guinea-Bissau", "Guinea-Bissau");
        putCountry("Guyana", "Guyana");
        putCountry("Honduras", "Honduras");
        putCountry("Hungary", "Hungary");
        putCountry("Iceland", "Iceland");
        putCountry("India", "India");
        putCountry("Iran", "Iran");
        putCountry("Iraq", "Iraq");
        putCountry("Ireland", "Ireland");
        putCountry("Israel", "Israel");
        putCountry("Italy", "Italy");
        putCountry("Jamaica", "Jamaica");
        putCountry("Japan", "Japan");
        putCountry("Jordan", "Jordan");
        putCountry("Kazakhstan", "Kazakhstan");
        putCountry("Kenya", "Kenya");
        putCountry("Kiribati", "Kiribati");
        putCountry("Kuwait", "Kuwait");
        putCountry("Kyrgyzstan", "Kyrgyzstan");
        putCountry("Laos", "Laos");
        putCountry("Latvia", "Latvia");
        putCountry("Lebanon", "Lebanon");
        putCountry("Lesotho", "Lesotho");
        putCountry("Liberia", "Liberia");
        putCountry("Libya", "Libya");
        putCountry("Liechtenstein", "Liechtenstein");
        putCountry("Lithuania", "Lithuania");
        putCountry("Luxembourg", "Luxembourg");
        putCountry("Macedonia", "Macedonia");
        putCountry("Madagascar", "Madagascar");
        putCountry("Malawi", "Malawi");
        putCountry("Maldives", "Maldives");
        putCountry("Mali", "Mali");
        putCountry("Marshall Islands", "Marshall Islands");
        putCountry("Mauritania", "Mauritania");
        putCountry("Mauritius", "Mauritius");
        putCountry("Mexico", "Mexico");
        putCountry("Moldova", "Moldova");
        putCountry("Mongolia", "Mongolia");
        putCountry("Montenegro", "Montenegro");
        putCountry("Morocco", "Morocco");
        putCountry("Mozambique", "Mozambique");
        putCountry("Namibia", "Namibia");
        putCountry("Nauru", "Nauru");
        putCountry("Nepal", "Nepal");
        putCountry("Netherlands", "Netherlands");
        putCountry("New Zealand", "New Zealand");
        putCountry("Nicaragua", "Nicaragua");
        putCountry("Niger", "Niger");
        putCountry("Nigeria", "Nigeria");
        putCountry("Norway", "Norway");
        putCountry("Pakistan", "Pakistan");
        putCountry("Palau", "Palau");
        putCountry("Panama", "Panama");
        putCountry("Papua New Guinea", "Papua New Guinea");
        putCountry("Paraguay", "Paraguay");
        putCountry("Peru", "Peru");
        putCountry("Philippines", "Philippines");
        putCountry("Poland", "Poland");
        putCountry("Portugal", "Portugal");
        putCountry("Qatar", "Qatar");
        putCountry("Romania", "Romania");
        putCountry("Saint Lucia", "Saint Lucia");
        putCountry("Saint Vincent and the Grenadines", "Saint Vincent and the Grenadines");
        putCountry("Samoa", "Samoa");
        putCountry("San Marino", "San Marino");
        putCountry("Saudi Arabia", "Saudi Arabia");
        putCountry("Senegal", "Senegal");
        putCountry("Serbia", "Serbia");
        putCountry("Seychelles", "Seychelles");
        putCountry("Sierra Leone", "Sierra Leone");
        putCountry("Singapore", "Singapore");
        putCountry("Slovakia", "Slovakia");
        putCountry("Slovenia", "Slovenia");
        putCountry("Solomon Islands", "Solomon Islands");
        putCountry("Somalia", "Somalia");
        putCountry("South Africa", "South Africa");
        putCountry("Spain", "Spain");
        putCountry("Sri Lanka", "Sri Lanka");
        putCountry("Sudan", "Sudan");
        putCountry("Suriname", "Suriname");
        putCountry("Swaziland", "Swaziland");
        putCountry("Sweden", "Sweden");
        putCountry("Switzerland", "Switzerland");
        putCountry("Syria", "Syria");
        putCountry("Tajikistan", "Tajikistan");
        putCountry("Tanzania", "Tanzania");
        putCountry("Thailand", "Thailand");
        putCountry("Togo", "Togo");
        putCountry("Tonga", "Tonga");
        putCountry("Trinidad and Tobago", "Trinidad and Tobago");
        putCountry("Tunisia", "Tunisia");
        putCountry("Turkey", "Turkey");
        putCountry("Turkmenistan", "Turkmenistan");
        putCountry("Tuvalu", "Tuvalu");
        putCountry("Uganda", "Uganda");
        putCountry("United Arab Emirates", "United Arab Emirates");
        putCountry("United Kingdom", "United Kingdom");
        putCountry("United States", "United States");
        putCountry("Uruguay", "Uruguay");
        putCountry("Uzbekistan", "Uzbekistan");
        putCountry("Vanuatu", "Vanuatu");
        putCountry("Vatican", "Vatican");
        putCountry("Venezuela", "Venezuela");
        putCountry("Vietnam", "Vietnam");
        putCountry("Yemen", "Yemen");
        putCountry("Zambia", "Zambia");
        putCountry("Zimbabwe", "Zimbabwe");
        putCountry("Abkhazia", "Abkhazia");
        putCountry("Cook Islands", "Cook Islands");
        putCountry("Kosovo", "Kosovo");
        putCountry("Karabakh", "Karabakh");
        putCountry("Niue", "Niue");
        putCountry("Northern Cyprus", "Northern Cyprus");
        putCountry("Ossetia", "Ossetia");

        countriesNamesList = new ArrayList<String>(countries.keySet());
    }

    private static void putCountry(String name, String... tags){
        countries.put(name, tags);
    }

    public static String[] getCountryTags(String name){
        return countries.get(name);
    }

    public static List<String> getCountries(){
        return countriesNamesList;
    }
}
