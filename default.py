# -*- coding: utf-8 -*-
# this file is released under public domain and you can use without limitations

#########################################################################
## This is a sample controller
## - index is the default action of any application
## - user is required for authentication and authorization
## - download is for downloading files uploaded in the db (does streaming)
## - call exposes all registered services (none by default)
#########################################################################

# Class code:

import json

MYSECRET = 'behappy'

#Five Games, duplicated so it's easy


#def game0():
#    secret = request.args(0)
#    if secret == MYSECRET:
#        open0 = request.vars.open
#        numPlayers0 = request.vars.numPlayers
#        turn0 = request.vars.turn
#        playA0 = request.vars.playA
#        playB0 = request.vars.playB
#        db.game0.drop();
#        db.game0.insert(
#            open=open0,
#            numPlayers=numPlayers0,
#            maxPlayers="2",
#            turn=turn0,
#            playA=playA0,
#            playB=playB0
#            )
#        row = db(db.game0).select().first()
#        return dict(test = "test")
#    else:
#        row = db(db.game0).select().first()
#        if row is not None:
#            #return dict(open = row.open, numPlayers = row.numPlayers, turn = row.turn, playA = row.playA, playB = row.playB)
#            return dict(status = "get")
#        else: return dict (status = "error")


#Saves a game to a specific slot (gameID), or creates a new file if necessary
def uploadGame():
    gameID0 = request.vars.gameID;
    open0 = request.vars.open
    numPlayers0 = request.vars.numPlayers
    turn0 = request.vars.turn
    playA0 = request.vars.playA
    playB0 = request.vars.playB
    #If there's an entry with this gameID, update it, otherwise make one
    db.game0.update_or_insert((db.game0.gameID==gameID0),
        gameID=gameID0,
        open=open0,
        numPlayers=numPlayers0,
        maxPlayers=2,
        turn=turn0,
        playA=playA0,
        playB=playB0
        )
    return dict(result='ok')

#Returns a specific closed game, or finds an open one and returns the gameID
def downloadGame():
    gameID0 = request.vars.gameID
    if gameID0 is not None:
        row = db(db.game0.gameID==gameID0).select().first()
        return dict(gameID = row.gameID, open = row.open, numPlayers = row.numPlayers, turn = row.turn, playA = row.playA, playB = row.playB)
    else:
        #If gameID is not given, find an open game
        row = db(db.game0.open=="True").select().first()
        if row is not None and row.gameID is not None:
            return dict(gameID = row.gameID, open = row.open, numPlayers = row.numPlayers, turn = row.turn, playA = row.playA, playB = row.playB)
        else:
            #No open games, tell them to make one!
            return dict(result='no open games')


def make_key_pair(a, k):
    return json.dumps([a, k])



def put():
    secret = request.args(0)
    #if secret != MYSECRET:
    #   raise HTTP(400)
    appid = request.args(1)
    key = request.args(2)
        #if appid is None or key is None:
        #raise HTTP(400)
    content = request.vars.c
    itemkey = make_key_pair(appid, key)
    db.store.update_or_insert(db.store.itemkey==itemkey,
                              itemkey=itemkey,
                              content=content)
    return dict(result='ok')


def get():
    secret = request.args(0)
        #if secret != MYSECRET:
        #raise HTTP(400)
    appid = request.args(1)
    key = request.args(2)
        #if appid is None or key is None:
        #raise HTTP(400)
    itemkey = make_key_pair(appid, key)
    row = db(db.store.itemkey == itemkey).select().first()
    r = None
    if row is not None:
        r = row.content
    return dict(result=r)


def verify_user(userid, username):
    """This function checks that the username has registered with the
    given userid. It is necessary, for instance, to prevent a user
    impersonating another."""
    r = db(db.userdata.userid == userid).select().first()
    return (r is not None and r.username == username)


def set_username():
    secret = request.vars.secret
        #if secret != MYSECRET:
        #raise HTTP(400)
    un = request.vars.username;
    userid = request.vars.userid;
        #if un is None or userid is None:
        #raise HTTP(400)
    rows = db(db.userdata.username == un).select()
    r = True
    for rec in rows:
        logger.info("Row: %r %r" % (rec.userid, rec.username))
        if rec.userid != userid:
            r = False
            break
    if r:
        # Stores that now the username is used by this userid.
        db.userdata.update_or_insert(db.userdata.userid == userid,
            userid=userid, username=un)
    logger.info("The result is: %r" % r)
    return dict(result=r, username=un)


def send():
    secret = request.vars.secret
        #if secret != MYSECRET:
        #raise HTTP(400)
    un = request.vars.username;
    userid = request.vars.userid;
    msg = request.vars.msg
    dest = request.vars.dest
        #if not verify_user(userid, un):
        #raise HTTP(400)
    db.message.insert(
        userid=userid,
        username=un,
        message=msg,
        dest=dest
        )
    return dict(result=request.vars)






################

def index():
    """
    example action using the internationalization operator T and flash
    rendered by views/default/index.html or views/generic.html

    if you need a simple wiki simply replace the two lines below with:
    return auth.wiki()
    """
    response.flash = T("Welcome to web2py!")
    return dict(message=T('Hello World'))


def user():
    """
    exposes:
    http://..../[app]/default/user/login
    http://..../[app]/default/user/logout
    http://..../[app]/default/user/register
    http://..../[app]/default/user/profile
    http://..../[app]/default/user/retrieve_password
    http://..../[app]/default/user/change_password
    http://..../[app]/default/user/manage_users (requires membership in
    use @auth.requires_login()
        @auth.requires_membership('group name')
        @auth.requires_permission('read','table name',record_id)
    to decorate functions that need access control
    """
    return dict(form=auth())

@cache.action()
def download():
    """
    allows downloading of uploaded files
    http://..../[app]/default/download/[filename]
    """
    return response.download(request, db)


def call():
    """
    exposes services. for example:
    http://..../[app]/default/call/jsonrpc
    decorate with @services.jsonrpc the functions to expose
    supports xml, json, xmlrpc, jsonrpc, amfrpc, rss, csv
    """
    return service()


@auth.requires_signature()
def data():
    """
    http://..../[app]/default/data/tables
    http://..../[app]/default/data/create/[table]
    http://..../[app]/default/data/read/[table]/[id]
    http://..../[app]/default/data/update/[table]/[id]
    http://..../[app]/default/data/delete/[table]/[id]
    http://..../[app]/default/data/select/[table]
    http://..../[app]/default/data/search/[table]
    but URLs must be signed, i.e. linked with
      A('table',_href=URL('data/tables',user_signature=True))
    or with the signed load operator
      LOAD('default','data.load',args='tables',ajax=True,user_signature=True)
    """
    return dict(form=crud())
