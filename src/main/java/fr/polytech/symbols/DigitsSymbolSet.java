package fr.polytech.symbols;

public class DigitsSymbolSet implements SymbolSet {
    @Override
    public int getSize() {
        return 10;
    }

    @Override
    public char[] getSymbols() {
        return new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    }
}
