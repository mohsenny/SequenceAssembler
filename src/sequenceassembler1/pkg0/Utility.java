/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceassembler1.pkg0;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author nypc
 */
public class Utility extends SequenceAssembler{
   
    public Utility()
    {
        super(inputs, window, match, outputText);
    }
    
    public static void resetMatchRecorder(int[][] match_recorder, int match_rule){ 
        for (int i = 0; i < match_rule; i++)
        {
            match_recorder[i][0] = -1;
            match_recorder[i][1] = -1;
        }
    }
    
    public static int getMapNotNullSize(Map<Integer, List<String>> map)
    {
        int size = 0;
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            List<String> val = (List)pair.getValue();
            if (pair.getValue() != null && val.size() != 0)
            {
                size++;
            }
        }
        
        return size;
    }
    
    public static boolean ifResultsAreAllOutputs(List<Integer> indices)
    {
        boolean checker = true;
        for (int i = 0; i < indices.size(); i++)
        {
            if (indices.get(i) <= inputSize - 1)
            {
                checker = false;
                return checker;
            }
            else
            {
                continue;
            }
        }
        
        return checker;
    }
    
    public static int findBiggerFragmentIndex(Map <Integer, List<String>> map, List<Integer> notNullIndices)
    {
        int biggerFragmentID = 0;
        int biggestSizeSoFar = 0;
        int currentSize = 0;
        for (int i = 0; i < notNullIndices.size(); i++)
        {
            int currentFragmentIndex = notNullIndices.get(i);
            currentSize = map.get(currentFragmentIndex).size();
            if (currentSize > biggestSizeSoFar)
            {
                biggerFragmentID = currentFragmentIndex;
                biggestSizeSoFar = currentSize;
            }
        }
        
        return biggerFragmentID;
    }
    
    public static List<Integer> getMapNotNullIncides(Map <Integer, List<String>> map)
    {
        List<Integer> indices = new ArrayList<Integer>();
        
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if (pair.getValue() != null)
            {
                indices.add((Integer)pair.getKey());
            }
        }
        return indices;
    }
    
    public static void PrepareForNextPhase(NLPHelper helper)
    {
        helper.Seqs_keys.clear();
        helper.Seqs_values.clear();
    }
    
    public static int findSimilarePairIndex(int host, int trav, List<String> SimilareFragments)
    {
        int result_index = 0;
        
        String key_1 = host + "-" + trav;
        String key_2 = trav + "-" + host;
                    
        for (int z = 0; z < SimilareFragments.size(); z++)
        {
            String fragmentPair = SimilareFragments.get(z);
            if (fragmentPair.equals(key_1))
            {
                result_index = z;
                break;
            }
            else if (fragmentPair.equals(key_2))
            {
                // Return -z; only to show that 
                result_index = z * -1;
                break;
            }
        }
        
        return result_index;
    }
    
    public static void WriteOutput(String msg, Color c, int size, boolean newLine)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        aset = sc.addAttribute(aset, StyleConstants.Size, size);

        int len = outputText.getDocument().getLength();
        outputText.setCaretPosition(len);
        outputText.setCharacterAttributes(aset, false);
        if (newLine)
            outputText.replaceSelection(msg + "\n");
        else
            outputText.replaceSelection(msg);
    }
    
    public static String makeFinalStory(int fragmentID, int fragmentTokensCount, NLPHelper helper )
    {
        List<String> final_result = new ArrayList<>();
        
        for (int token = 0; token < fragmentTokensCount ; token++)
        {
            final_result.add(helper.fragmentsKeyowrdSetntenceMapper.get(fragmentID).get(token).toString());
        }
        
        HashSet<String> final_result_unique = new LinkedHashSet<>();
        
        String final_result_str = "";
        for (String s : final_result){
            final_result_unique.add(s);
        }
        // Creating a string from final_result_unique
        for (String s : final_result_unique){
            final_result_str += s;
        }  
        
        return final_result_str;
    }
            
    public static void writeHighlightedResult (String finalStory, List<String> fragments, List<String> pureURIs) throws IOException
    {
        Map<Integer, int[]> colors = new HashMap<Integer, int[]>();
        InitialColorsManually(colors);
        
        finalStory = finalStory.replaceAll("\\.","\\. ");
        finalStory = finalStory.replaceAll("\\,","\\, ");
        //String ResultWords[] = finalStory.split("[\\n\\s+]");
        String ResultWords[] = finalStory.split("\\s+");
        List<String> WordsList = Arrays.asList(ResultWords);
        ListIterator<String> wordsIter = WordsList.listIterator();
        
        String currentFragment;
        
        while (wordsIter.hasNext())
        {
            String currentSearchKey = "";
            int[] occurances = new int[inputSize];
            String[] occurancesStrings = new String[inputSize];
            
            if (!wordsIter.hasPrevious())   // Otherwise,it has already gone to the next one in the last loop (fail search)
            {
                currentSearchKey += wordsIter.next();
            }
            for (int f = 0; f < inputSize; f++)
            {
                currentFragment = readFile(pureURIs.get(f), Charset.defaultCharset());
                //currentFragment = currentFragment.replaceAll("\\n", "\\. ");
                currentFragment = currentFragment.replaceAll("\\s{2,}", " ");
                
                String currentFragmentTemp = currentFragment.replaceAll("\\.", "");
                String currentSearchKeyTemp = currentSearchKey;
                
                while (currentFragmentTemp.toLowerCase().contains(currentSearchKeyTemp.toLowerCase()))
                //while(currentFragment.toLowerCase().matches())
                {
                    occurancesStrings[f] = currentSearchKey;
                    occurances[f]++;
                    if (wordsIter.hasNext())
                    {
                        if (currentSearchKey.length() > 0)
                        {
                            currentSearchKey += " "+wordsIter.next();
                        }
                        else
                        {
                            //in case of being the first searchKey of each iteration
                            currentSearchKey += wordsIter.next();
                        }
                        currentSearchKeyTemp = currentSearchKey.replaceAll("\\.", "");
                    }
                    else
                    {
                        // If there is no more word in the finalResult to be highlighted, then print it all out
                        break;
                    }
                }
            }
            // There is no need to do the following if it's the last one.
            if (wordsIter.hasNext())
            {
                // Move the iterator one step backward, since in the last step we went forward once and it didn't match
                wordsIter.previous();
            }
            int fragWithHighOccur = findMaxIndex(occurances);
            int[] RGBcolor = colors.get(fragWithHighOccur);
            float[] HSBcolor = Color.RGBtoHSB(RGBcolor[0], RGBcolor[1], RGBcolor[2], null);
            Color color = Color.getHSBColor(HSBcolor[0], HSBcolor[1], HSBcolor[2]);
            if (occurancesStrings[fragWithHighOccur] == "" || occurancesStrings[fragWithHighOccur] == " ")
            {
                //Highlighting process has failed
                WriteOutput("[Due to not considering proper spacing and/or punctuation, highlighting process cannot go any further.]", Color.RED, 14, false); 
                return;
            }
            else
            {
                WriteOutput(occurancesStrings[fragWithHighOccur], color, 12, false); 
            }
        
        }
        
        printColorMap(colors);
    }
    
    public static void InitialColors (Map<Integer, int[]> colors)
    {
        Random rand;
        //Color randomColor;
        int r;
        int g;
        int b;
        
        for (int i = 0; i < inputSize; i++)
        {
            rand = new Random();
            //{
                r = rand.nextInt(200);
                g = rand.nextInt(200);
                b = rand.nextInt(200);
            //}while(r < 200 && g < 200 && b < 200)   // Make sure colors are readable and not too bright
            //randomColor = new Color(r, g, b);
            int[] randomColor = {r, g, b};
            colors.put(i, randomColor);
        }
    }
    
    public static void InitialColorsManually (Map<Integer, int[]> colors)
    {
        colors.put(0, new int[] {102, 0, 0});
        colors.put(1, new int[] {76, 153, 0});
        colors.put(2, new int[] {0, 255, 255});
        colors.put(3, new int[] {255, 102, 178});
        colors.put(4, new int[] {0, 0, 0});
        colors.put(5, new int[] {102, 51, 255});
        colors.put(6, new int[] {229, 204, 0});
        colors.put(7, new int[] {255, 0, 255});
        colors.put(8, new int[] {255, 0, 0});
        colors.put(9, new int[] {0, 204, 0});
        
        colors.put(10, new int[] {102, 102, 255});
        colors.put(11, new int[] {0, 102, 102});
        colors.put(12, new int[] {0, 153, 153});
        colors.put(13, new int[] {255, 255, 0});
        colors.put(14, new int[] {102, 178, 255});
        colors.put(15, new int[] {255, 102, 178});
        colors.put(16, new int[] {0, 76, 153});
        colors.put(17, new int[] {96, 96, 96});
        colors.put(18, new int[] {153, 153, 0});
        colors.put(19, new int[] {255, 153, 153});
        
        colors.put(20, new int[] {153, 255, 51});
        colors.put(21, new int[] {204, 255, 204});
        //colors.put(0, new int[] {102, 0, 0});
        //colors.put(0, new int[] {102, 0, 0});
        //colors.put(0, new int[] {102, 0, 0});
        //colors.put(0, new int[] {102, 0, 0});
        //colors.put(0, new int[] {102, 0, 0});
        //colors.put(0, new int[] {102, 0, 0});
        //colors.put(0, new int[] {102, 0, 0});
    }
    
    public static String readFile(String path, Charset encoding) 
    throws IOException 
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
    
    public static int findMaxIndex(int[] occurances)
    {
        int maxValue = 0;
        int maxIndex = 0;
        for (int i = 0; i < occurances.length; i++)
        {
            if (occurances[i] > maxValue)
            {
                maxValue = occurances[i];
                maxIndex = i;
            }
        }
        return maxIndex;    
    }
    
    public static void printColorMap(Map<Integer, int[]> colors)
    {
        WriteOutput("\n", Color.RED, 1, true);
        WriteOutput("\n", Color.RED, 1, true);
        Integer fragmentID;
        WriteOutput("Inputs' color map :", Color.RED, 14, true);
        for (Map.Entry<Integer, int[]> entry : colors.entrySet())
        {
            fragmentID = entry.getKey();
            if   (fragmentID < inputSize){
                fragmentID++;
                int[] RGBcolor = entry.getValue();

                float[] HSBcolor = Color.RGBtoHSB(RGBcolor[0], RGBcolor[1], RGBcolor[2], null);
                Color color = Color.getHSBColor(HSBcolor[0], HSBcolor[1], HSBcolor[2]);

                WriteOutput("Inputs " + fragmentID.toString() , color, 12, true);
            }
        }
    }
    
}