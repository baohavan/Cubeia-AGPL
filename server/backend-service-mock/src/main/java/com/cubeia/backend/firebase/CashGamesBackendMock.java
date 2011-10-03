package com.cubeia.backend.firebase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.PlayerSessionIdImpl;
import com.cubeia.backend.cashgame.TableIdImpl;
import com.cubeia.backend.cashgame.callback.AnnounceTableCallback;
import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.backend.cashgame.dto.AnnounceTableRequest;
import com.cubeia.backend.cashgame.dto.AnnounceTableResponse;
import com.cubeia.backend.cashgame.dto.BalanceUpdate;
import com.cubeia.backend.cashgame.dto.BatchHandRequest;
import com.cubeia.backend.cashgame.dto.BatchHandResponse;
import com.cubeia.backend.cashgame.dto.CloseSessionRequest;
import com.cubeia.backend.cashgame.dto.CloseTableRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse;
import com.cubeia.backend.cashgame.dto.ReserveRequest;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.action.service.ServiceAction;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.RoutableService;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class CashGamesBackendMock implements CashGamesBackendContract, Service, RoutableService {
    
    private Logger log = LoggerFactory.getLogger(CashGamesBackendMock.class);
    
    private final AtomicInteger idSequence = new AtomicInteger(0);

    private final Multimap<PlayerSessionId, Integer> sessionTransactions = 
        Multimaps.<PlayerSessionId, Integer>synchronizedListMultimap(ArrayListMultimap.<PlayerSessionId, Integer>create());

    private ServiceRouter router;
    
    private int nextId() {
        return idSequence.incrementAndGet();
    }
    
    @Override
    public FirebaseCallbackFactory getCallbackFactory() {
        return new FirebaseCallbackFactoryImpl(router);
    }
    
    @Override
    public void announceTable(AnnounceTableRequest request, AnnounceTableCallback callback) {
        AnnounceTableResponse response = new AnnounceTableResponse(new TableIdImpl());
        log.debug("new table approved, tId = {}", response.tableId);
        callback.requestSucceded(response);
    }

    @Override
    public void closeTable(CloseTableRequest request) {
        log.debug("table removed: {}", request.tableId);
    }

    @Override
    public void openSession(OpenSessionRequest request, OpenSessionCallback callback) {
        PlayerSessionId sessionId = new PlayerSessionIdImpl(request.playerId);
        sessionTransactions.put(sessionId, 0);
        
        OpenSessionResponse response = new OpenSessionResponse(sessionId, 
            Collections.<String, String>emptyMap());
        log.debug("new session opened, tId = {}, pId = {}, sId = {}", 
            new Object[] {request.tableId, request.playerId, response.sessionId});
        log.debug("currently open sessions: {}", sessionTransactions.size());
        callback.requestSucceded(response);
//        sendToTable(gameId, request.tableId.id, response);
    }

    @Override
    public void closeSession(CloseSessionRequest request) {
        log.warn("not implemented!");
        
        PlayerSessionId sid = request.playerSessionId;
        
        if (!sessionTransactions.containsKey(sid)) {
            log.error("error closing session {}: not found", sid);
        } else {
            sessionTransactions.removeAll(sid);
        }
    }

    @Override
    public void reserve(ReserveRequest request, ReserveCallback callback) {
        int amount = request.amount;
        PlayerSessionId sid = request.playerSessionId;
        
        if (sessionTransactions.containsKey(sid)) {
            log.error("reserve failed, session not found: sId = " + sid);
            ReserveFailedResponse failResponse = new ReserveFailedResponse(request.playerSessionId, 
                ReserveFailedResponse.ErrorCode.A, "session " + sid + " not open");
            callback.requestFailed(failResponse);
        } else {
            sessionTransactions.put(sid, amount);
            int newBalance = getBalance(sid);
            BalanceUpdate balanceUpdate = new BalanceUpdate(request.playerSessionId, newBalance, nextId());
            ReserveResponse response = new ReserveResponse(balanceUpdate, amount);
            log.debug("reserve successful: sId = {}, amount = {}, new balance = {}" + sid, amount, newBalance);
            callback.requestSucceded(response);
        }
    }

    @Override
    public BatchHandResponse batchHand(BatchHandRequest request) {
        log.warn("not implemented!");
        return new BatchHandResponse();
    }

    @Override
    public long getMainAccountBalance(int playerId) {
        log.warn("not implemented!");
        return 500000;
    }

    private int getBalance(PlayerSessionId sid) {
        int balance = 0;
        for (Integer tx : sessionTransactions.get(sid)) {
            balance += tx;
        }
        return balance;
    }
    
    @Override
    public BalanceUpdate getSessionBalance(PlayerSessionId sessionId)
    		throws GetBalanceFailedException {
    	return new BalanceUpdate(sessionId, getBalance(sessionId), nextId());
    }
    
    @Override
    public void setRouter(ServiceRouter router) {
        this.router = router;
    }
    
    @Override
    public void onAction(ServiceAction e) {
        // nothing should arrive here
    }
    
    @Override
    public void init(ServiceContext con) throws SystemException {
        log.debug("service init");
    }

    @Override
    public void destroy() {
        log.debug("service destroy");
    }

    @Override
    public void start() {
        log.debug("service start");
    }

    @Override
    public void stop() {
        log.debug("service stop");
    }

}
