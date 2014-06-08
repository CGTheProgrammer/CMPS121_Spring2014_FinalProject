# Table for storing key/value for each app.

import datetime

db.define_table('store',
    Field('itemkey'),
    Field('content', 'text'),
    )


db.define_table('userdata',
    Field('userid'),
    Field('username'),            
    )

db.define_table('message',
    Field('sent_on', 'datetime', default=datetime.datetime.utcnow()),
    Field('userid'),
    Field('username'),
    Field('dest'),
    Field('message', 'text'),
    )

db.define_table('game0',
    Field('gameID'),
    Field('open'),
    Field('numPlayers'),
    Field('maxPlayers'),
    Field('turn'),
    Field('playA'),
    Field('playB')
    )

