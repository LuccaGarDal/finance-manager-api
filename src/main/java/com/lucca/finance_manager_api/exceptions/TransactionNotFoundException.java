package com.lucca.finance_manager_api.exceptions;

public class TransactionNotFoundException extends RuntimeException{
    public TransactionNotFoundException () {super("Transaction not found");}

    public TransactionNotFoundException (String message) {super(message);}
}
