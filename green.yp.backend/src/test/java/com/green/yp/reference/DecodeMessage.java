package com.green.yp.reference;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.*;

public class DecodeMessage {

  private static final String DELIMTER = " ";

  public String decode(String encodedString) throws IOException {
    return decode(new StringReader(encodedString));
  }

  public String decode(StringReader encodedReader) throws IOException {
    return decode(new LineNumberReader(encodedReader));
  }

  public String decode(LineNumberReader lineNumberReader) throws IOException {
    Map<Integer, String> encodedMap = readEncodedMessage(lineNumberReader);
    Integer[] sortedKeys = getSortedKeys(encodedMap);
    StringBuilder decodedMessage = new StringBuilder();
    int level = 1;
    int index = level;
    while (index <= sortedKeys.length) {
      decodedMessage.append(encodedMap.get(sortedKeys[index - 1])).append(DELIMTER);
      level++;
      index += level;
    }
    return decodedMessage.toString().trim();
  }

  private Integer[] getSortedKeys(Map<Integer, String> encodedMap) {
    Set<Integer> keySet = encodedMap.keySet();
    List<Integer> sortedKeys = new ArrayList<Integer>(keySet);
    sortedKeys.sort(Integer::compareTo);
    return sortedKeys.toArray(new Integer[sortedKeys.size()]);
  }

  private Map<Integer, String> readEncodedMessage(LineNumberReader encodedReader)
      throws IOException {
    String line;
    Map<Integer, String> lineMap = new HashMap<>();
    while ((line = encodedReader.readLine()) != null) {
      String[] parts = line.split(DELIMTER);
      lineMap.put(Integer.valueOf(parts[0]), parts[1]);
    }
    return lineMap;
  }
}
