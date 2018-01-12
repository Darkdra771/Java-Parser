/* *** This file is given as part of the programming assignment. *** */
import java.util.Stack;

public class Parser {


    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    private Table symbolTable;
    private String condition = "";
    private static Stack<String> function = new Stack<String>();
    public static Stack<String> myCondition = new Stack<String>(); //Stacks to store condition
    private char prefix = 'a' - 1;
    private char secondNumber = ' ';

    private void scan() {
	      tok = scanner.scan();
    }

    private Scan scanner;

    Parser(Scan scanner) {
      System.out.println("#include <stdio.h>");
      System.out.print("void main(void)");
	     this.scanner = scanner;
       function.push("");
       symbolTable = new Table();
	     scan();
	     program();
	     if( tok.kind != TK.EOF )
	      parse_error("junk after logical end of program");
    }

    private void program() {
       System.out.println("{");
	     block();
       System.out.println("}");
    }

    private void block(){

      this.prefix++;
      symbolTable.newBlock();
	   declaration_list();
	   statement_list();

       this.prefix--;
       symbolTable.deleteBlock();
    }

    private void declaration_list() {
	// below checks whether tok is in first set of declaration.
	// here, that's easy since there's only one token kind in the set.
	// in other places, though, there might be more.
	// so, you might want to write a general function to handle that.
	   while( is(TK.DECLARE) ) {
	      declaration();
	   }
    }

    private void statement_list(){
      while( is(TK.IF) || is(TK.PRINT) || is(TK.DO) || is(TK.TILDE) || is(TK.ID) || is(TK.FOR)){
        statement();
      }
    }

    private void declaration() {
	     mustbe(TK.DECLARE);
	String temp = this.prefix + "_" + tok.string;
	boolean redeclare = false;
       if (symbolTable.find(tok.string)){
         System.err.println("redeclaration of variable " + tok.string);
	redeclare = true;
       }
       symbolTable.addVariable(tok.string);
	if(!redeclare){
		System.out.print("int " + this.prefix + "_" + tok.string);
	}
	     mustbe(TK.ID);
	   while( is(TK.COMMA) ) {
		if(redeclare){
			System.out.print("int ");
		}
	         scan();
           if (symbolTable.find(tok.string)){
             System.err.println("redeclaration of variable " + tok.string);
           }
           else {
             symbolTable.addVariable(tok.string);
             if(!redeclare){System.out.print(", ");
	     }
             System.out.print(this.prefix + "_" + tok.string);
           }
		redeclare = false;
	          mustbe(TK.ID);
	      }
        if ( function.peek() != "for"){
          System.out.println(";");}
    }

    private void multop(){
      if (tok.kind != TK.DIVIDE){
        System.out.print("*");
        mustbe(TK.TIMES);
      }
      else{
        System.out.print("/");
        mustbe(TK.DIVIDE);
      }
    }
    private void addop(){
      if(tok.kind != TK.PLUS){
        if (function.peek() == "do"){this.condition += "-";}
        else {System.out.print("- ");
	}
        mustbe(TK.MINUS);
      }
      else{
        if (function.peek() == "do"){this.condition += "+";}
        else {System.out.print("+ ");
	}
        mustbe(TK.PLUS);
      }
    }

    private void print(){
      function.push("print"); //in printing function
      System.out.print("printf(\"%d\\n\", ");
      mustbe(TK.PRINT);
      expr();
      System.out.println(");");
      function.pop();
    }

    private void do_Parse(){
      function.push("do");
      System.out.println("do{");
      mustbe(TK.DO);
      guarded_command();
      mustbe(TK.ENDDO);
      this.condition = myCondition.pop();
      System.out.println("}while((" + this.condition + ") <= 0);");
      this.condition = "";
      function.pop();
    }

    private void guarded_command(){
      expr();
      if (function.peek() != "do"){
        System.out.println("){");
      }
      mustbe(TK.THEN);
      if (function.peek() != "if"){
      	myCondition.push(this.condition);
      }
      this.condition = "";
      block();
      if (this.function.peek() != "do" && function.peek() != "assign"){
        System.out.println("}");
      }
    }

    private void expr(){
      if (function.peek() == "if"){System.out.print("(");
	}
      term();
      while( is(TK.PLUS) || is(TK.MINUS)){
        addop();
        term();
      }
      if (function.peek() == "if") {System.out.print(") <= 0");
	}
    }

    private void term(){
      factor();
      while( is(TK.TIMES) || is(TK.DIVIDE)){
        multop();
        factor();
      }
    }

    private void factor(){
      if( is(TK.LPAREN)){
        System.out.print("(");
        mustbe(TK.LPAREN);
        expr();
        System.out.print(")");
        mustbe(TK.RPAREN);
      }
      else if( is(TK.NUM)){
        if(function.peek() == "print") {System.out.print(tok.string);
	}
        else if(function.peek() == "if") {
            System.out.print(tok.string);
        }
        else if(function.peek() == "assign"){System.out.print(tok.string);
	}
        else if(function.peek() == "do"){this.condition += tok.string;}
        else if(function.peek() == "for"){System.out.print(tok.string);}
        mustbe(TK.NUM);
      }
      else{
        ref_id();
      }
    }

    private void ref_id(){
      boolean findLevel = false;
      symbolTable.number = '-';
      if( is(TK.TILDE)){
        findLevel = true;
        symbolTable.number = 'G';
        mustbe(TK.TILDE);
        if( is(TK.NUM)){
          symbolTable.number = tok.string.charAt(0);
          if ((tok.string).length() != 1){secondNumber = tok.string.charAt(1);}
          mustbe(TK.NUM);
        }
      }
      if (findLevel){
        if(!symbolTable.findInLevel(tok.string)){
          if (symbolTable.number == '-' || symbolTable.number == 'G'){System.err.println("no such variable ~" + tok.string + " on line " + tok.lineNumber);}
          else if(secondNumber != ' '){System.err.println("no such variable ~" + symbolTable.number + secondNumber + tok.string + " on line " + tok.lineNumber);}
          else {System.err.println("no such variable ~" + symbolTable.number + tok.string + " on line " + tok.lineNumber);}
          System.exit(1);
        }
      }
      else if (!symbolTable.empty() && !symbolTable.findInStack(tok.string) && is(TK.ID)){
        System.err.println(tok.string + " is an undeclared variable on line " + tok.lineNumber);
        System.exit(1);
      }
      char temp = this.prefix;
      this.prefix = symbolTable.prefix;
      if (function.peek() == "do"){ this.condition += (this.prefix + "_" + tok.string);}
      else{
          System.out.print(this.prefix + "_");
          System.out.print(tok.string);
      }
      this.prefix = temp;
      mustbe(TK.ID);
    }

    private void assignment(){
      function.push("assign");
      ref_id();
      System.out.print("= ");
      mustbe(TK.ASSIGN);
      expr();
      function.pop();
      if (function.peek() == "for"){}
      else {System.out.println(";");}
    }

    private void if_Parse(){
      function.push("if");
      System.out.print("if(");
      mustbe(TK.IF);
      guarded_command();
      while( is(TK.ELSEIF)){
        function.push("if");
        System.out.print("else if(");
        mustbe(TK.ELSEIF);
        guarded_command();
        function.pop();
      }
      if( is(TK.ELSE)){
        System.out.println("else {");
        mustbe(TK.ELSE);
        block();
        System.out.println("}");
      }
      mustbe(TK.ENDIF);
      function.pop();
    }

    private void for_parse(){
      mustbe(TK.FOR);
      function.push("for");
      System.out.print("for(");
      assignment();
      System.out.print(";");
      mustbe(TK.SEMICOLON);
      expr();
      System.out.print("<= 0;");
      mustbe(TK.SEMICOLON);
      statement();
      mustbe(TK.THEN);
      System.out.println("){");
      function.push("next");
      block();
      function.pop();
      mustbe(TK.ENDFOR);
      function.pop();
      System.out.println("}");
    }

    private void statement(){
      if ( is(TK.IF)){
        if_Parse();
      }
      else if( is(TK.DO)){
        do_Parse();
      }
      else if( is(TK.PRINT)){
        print();
      }
      else if( is(TK.FOR)){
        for_parse();
      }
      else{
        assignment();
      }
    }

    // is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    }

    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
	     if( tok.kind != tk ) {
	    System.err.println( "mustbe: want " + tk + ", got " +
				    tok);
	    parse_error( "missing token (mustbe)" );
	    }
	   scan();
    }

    private void parse_error(String msg) {
	     System.err.println( "can't parse: line "
			    + tok.lineNumber + " " + msg );
	         System.exit(1);
    }
}
