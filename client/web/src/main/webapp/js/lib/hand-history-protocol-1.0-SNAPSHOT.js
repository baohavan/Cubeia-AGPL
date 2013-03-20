// I AM AUTO-GENERATED, DON'T CHECK ME INTO SUBVERSION (or else...)

var com = com || {};
com.cubeia = com.cubeia || {};
com.cubeia.games = com.cubeia.games || {};
com.cubeia.games.poker = com.cubeia.games.poker || {};
com.cubeia.games.poker.handhistoryservice = com.cubeia.games.poker.handhistoryservice || {};
com.cubeia.games.poker.handhistoryservice.io = com.cubeia.games.poker.handhistoryservice.io || {};
com.cubeia.games.poker.handhistoryservice.io.protocol = com.cubeia.games.poker.handhistoryservice.io.protocol || {};


com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHand = function () {
    this.classId = function () {
        return com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHand.CLASSID
    };
    this.handId = {};
    this.save = function () {
        var a = new FIREBASE.ByteArray();
        a.writeString(this.handId);
        return a
    };
    this.load = function (a) {
        this.handId = a.readString()
    };
    this.getNormalizedObject = function () {
        var a = {};
        var b;
        a.summary = "com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHand";
        a.details = {};
        a.details.handId = this.handId;
        return a
    }
};
com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHand.CLASSID = 2;
com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHandIds = function () {
    this.classId = function () {
        return com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHandIds.CLASSID
    };
    this.tableId = {};
    this.count = {};
    this.time = {};
    this.save = function () {
        var a = new FIREBASE.ByteArray();
        a.writeInt(this.tableId);
        a.writeInt(this.count);
        a.writeString(this.time);
        return a
    };
    this.load = function (a) {
        this.tableId = a.readInt();
        this.count = a.readInt();
        this.time = a.readString()
    };
    this.getNormalizedObject = function () {
        var a = {};
        var b;
        a.summary = "com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHandIds";
        a.details = {};
        a.details.tableId = this.tableId;
        a.details.count = this.count;
        a.details.time = this.time;
        return a
    }
};
com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHandIds.CLASSID = 1;
com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHandSummaries = function () {
    this.classId = function () {
        return com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHandSummaries.CLASSID
    };
    this.tableId = {};
    this.count = {};
    this.time = {};
    this.save = function () {
        var a = new FIREBASE.ByteArray();
        a.writeInt(this.tableId);
        a.writeInt(this.count);
        a.writeString(this.time);
        return a
    };
    this.load = function (a) {
        this.tableId = a.readInt();
        this.count = a.readInt();
        this.time = a.readString()
    };
    this.getNormalizedObject = function () {
        var a = {};
        var b;
        a.summary = "com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHandSummaries";
        a.details = {};
        a.details.tableId = this.tableId;
        a.details.count = this.count;
        a.details.time = this.time;
        return a
    }
};
com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHandSummaries.CLASSID = 4;
com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHands = function () {
    this.classId = function () {
        return com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHands.CLASSID
    };
    this.tableId = {};
    this.count = {};
    this.time = {};
    this.save = function () {
        var a = new FIREBASE.ByteArray();
        a.writeInt(this.tableId);
        a.writeInt(this.count);
        a.writeString(this.time);
        return a
    };
    this.load = function (a) {
        this.tableId = a.readInt();
        this.count = a.readInt();
        this.time = a.readString()
    };
    this.getNormalizedObject = function () {
        var a = {};
        var b;
        a.summary = "com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHands";
        a.details = {};
        a.details.tableId = this.tableId;
        a.details.count = this.count;
        a.details.time = this.time;
        return a
    }
};
com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHands.CLASSID = 3;
com.cubeia.games.poker.handhistoryservice.io.protocol.ProtocolObjectFactory = {};
com.cubeia.games.poker.handhistoryservice.io.protocol.ProtocolObjectFactory.create = function (c, a) {
    var b;
    switch (c) {
        case com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHandIds.CLASSID:
            b = new com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHandIds();
            b.load(a);
            return b;
        case com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHand.CLASSID:
            b = new com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHand();
            b.load(a);
            return b;
        case com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHands.CLASSID:
            b = new com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHands();
            b.load(a);
            return b;
        case com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHandSummaries.CLASSID:
            b = new com.cubeia.games.poker.handhistoryservice.io.protocol.HandHistoryProviderRequestHandSummaries();
            b.load(a);
            return b
    }
    return null
};