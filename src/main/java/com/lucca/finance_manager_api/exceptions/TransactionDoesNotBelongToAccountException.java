package com.lucca.finance_manager_api.exceptions;

public class TransactionDoesNotBelongToAccountException extends RuntimeException{
    public TransactionDoesNotBelongToAccountException () {super("Transaction does not belong to this account");}

    public TransactionDoesNotBelongToAccountException (String message) {super(message);}
}
