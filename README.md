# tr-test
atomikos spring activemq jta oracle

http://www.atomikos.com/Documentation/SpringIntegration

In order to maximize recovery and shutdown/restart ability, it is highly recommended that you configure your Spring configuration with the following init dependencies:

Make the transaction manager depends on all your JDBC and JMS connection factories: this ensures that connections are kept until AFTER the transaction manager has terminated all transactions during shutdown.
Make any MessageDrivenContainer depend on the transaction manager, to ensure that all JMS listener sessions have terminated before the transaction manager starts shutting down.

http://www.atomikos.com/Documentation/ConfiguringOracle, http://www.atomikos.com/Documentation/ConfiguringOracleForXA

````sql
grant select on sys.dba_pending_transactions to <user name>;
grant select on sys.pending_trans$ to <user name>;
grant select on sys.dba_2pc_pending to <user name>;
grant execute on sys.dbms_system to <user name>;
````
