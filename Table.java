import java.util.Stack;
import java.util.LinkedList;
import java.lang.*;

public class Table{

  public static Stack<LinkedList<String>> myTable = new Stack<LinkedList<String>>();
  public static char number;
  public static int levels;
  public char prefix;
  private char prefixLevel;

  public void Table(){ //makes new linked LinkedList

    LinkedList<String> myList = new LinkedList<String>();

    myTable.push(myList);
    levels = 0;
    prefixLevel = 'a';

  }

  public void addVariable(String name){

      LinkedList<String> myList = new LinkedList<String>(myTable.pop());
      myList.add(name);
      myTable.push(myList);

  }

  public boolean findInStack(String name){
    boolean found = false;
    int num = 0;

    Stack<LinkedList<String>> temp = new Stack<LinkedList<String>>();
    LinkedList<String> myList = new LinkedList<String>(myTable.peek());
    if (myList.contains(name)){
      found = true;
    }

    while (!found){
      num++;
      temp.push(myTable.pop());
      if (myTable.empty()){break;}
      myList = myTable.peek();
      if (myList.contains(name)){
        found = true;
      }
    }

    while (!temp.empty()){
      myTable.push(temp.pop());
    }

    if (found){
      int i = 96 + levels - num ;
      prefix = (char)i;
    }

    return found;
  }

  public boolean findInLevel(String name){

    int num;

    if (number != 'G'){
      num = Integer.parseInt(Character.toString(number));
    }
    else {
      num = levels - 1;
    }

	int tempo = num;

    if (num > levels){
      return false;
    }

    boolean found = false;
    Stack<LinkedList<String>> temp = new Stack<LinkedList<String>>();

    while (num != 0){
      if (!myTable.empty()){
        temp.push(myTable.pop());
      }
      num--;
    }

    if (!myTable.empty()){
      LinkedList<String> myList = new LinkedList<String>(myTable.peek());
      if (myList.contains(name)){
        found = true;
      }
    }
    while (!temp.empty()){
      myTable.push(temp.pop());
    }

    if (found){
      int i = /*(int)prefixLevel - */ 96 + levels - tempo;
      //System.out.println(i);
      prefix = (char)i;
    }

    return found;
  }

  public boolean find(String name){

    if (myTable.empty()){
      return false;
    }

    LinkedList<String> myList = new LinkedList<String>(myTable.peek());
    return myList.contains(name);
  }

  public void newBlock(){
    LinkedList<String> myList = new LinkedList<String>();
    myTable.push(myList);
    levels++;
    prefixLevel++;
  }

  public void deleteBlock(){
    myTable.pop();
    levels--;
    prefixLevel--;
  }

  public boolean empty(){
    return myTable.empty();
  }

}
