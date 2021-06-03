/**
 * Class to read from csv file
 * File has to have a header and the lines have to be divided by Komma, and following this structure:
 * product, rating, helpful, reviewdate, user, summary, content
 * @version 02-06-2021
 */
package csv;

import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import main.Config;
import java.util.Scanner;

public class CSV{

    private ArrayList<String[]> csvFile = new ArrayList<String[]>();

    public CSV(){
        //System.out.println("\033[1;34m*\033[35m*\033[33m*\033[32m* \033[91m csv_file start \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");
    }

    public void addToCSV(String[] str){
        csvFile.add(str);
    }

    public ArrayList<String[]> getFile(){
        return this.csvFile;
    }    

    /**
     * Read from file
     * Split into String-Arrays
     * call on functions for proper file data
     */
    public void readFile() throws FileNotFoundException {
        File file = new File(Config.class.getResource("").getPath() + "/../../data/reviews.csv");
        Scanner sc = new Scanner(file);
        ArrayList<String> lineList = new ArrayList<String>();
        while (sc.hasNextLine()){
            lineList.add(sc.nextLine());
        }
        sc.close();

        for(String s : lineList) {
            addToCSV(s.split("\",\""));
        }
        //remove Header
        csvFile.remove(0); 
        trimQuotes();
        changeMod();
        System.out.println("\033[1;34m*\033[35m*\033[33m*\033[32m* \033[91m csv_file read in \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");
    }

    /**
     * Function to remove quotes at the Beginning und end.
     */
    private void trimQuotes(){
        for(String[] line : csvFile){
            line[0] = line[0].substring(1, line[0].length());
            line[6] = line[6].substring(0,line[6].length()-1);
        }
    }

    /**
     * Function to check the file for <br>, <p>, " and &
     * transforms them to a properly displayable format
     */
    private void changeMod(){
        for(String[] line : csvFile){
            for(int i=0; i<line.length; i++){
                boolean stop = false;
                int indexBR = -1;
                int indexP = -1;
                int indexQ = -1;
                int indexA = -1;
                while(!stop){
                    indexBR = line[i].indexOf("&lt;BR&gt;");
                    if(indexBR != -1){
                        int place = line[i].indexOf("&lt;BR&gt;");
                        String s = line[i].substring(0, place) + "\n" + line[i].substring(place+10);
                        line[i] = s;
                    }
                    indexP = line[i].indexOf("&lt;P&gt;");
                    if(indexP != -1){
                        int place = line[i].indexOf("&lt;P&gt;");
                        String s = line[i].substring(0, place) + "\n" + line[i].substring(place+9);
                        line[i] = s;
                    }
                    indexQ = line[i].indexOf("&quot");
                    if(indexQ != -1){
                        int place = line[i].indexOf("&quot;");
                        String s = line[i].substring(0, place) + "\uFF02" + line[i].substring(place+6);
                        line[i] = s;
                    }
                    indexA = line[i].indexOf("&amp");
                    if(indexA != -1){
                        int place = line[i].indexOf("&amp");
                        String s = line[i].substring(0, place) + "\u0026" + line[i].substring(place+5);
                        line[i] = s;
                    }
                    else if ((indexBR==-1) && (indexP==-1) && (indexQ==-1) && (indexA==-1)){
                        stop = true;
                    }
                }
            }
        }
    }




    public static void main(String[] args) throws FileNotFoundException {

        CSV csv = new CSV();
        csv.readFile();

        for(String[] line : csv.getFile()){
            for(String s : line){
                System.out.println(s);
            }
            System.out.println("*");
        }
    }
}


