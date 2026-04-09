package com.lucca.finance_manager_api.exceptions;

public class InsufficientBalanceException extends RuntimeException{
    public InsufficientBalanceException () {super("Insufficient Balance for transaction");}

    public InsufficientBalanceException (String message ) {super(message);}
}
