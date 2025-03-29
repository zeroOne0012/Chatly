# Chatly
## Access Rights & etc

### Member & Auth
- GET..: login
- POST(login, register): X 
- PUT: Owner
- DELETE: Owner

### Channel
- GET..: login
- POST: login
- PUT: ADMIN
- DELETE: ADMIN; Cascade-ChannelMember

### ChannelMember
- GET all: login (for dev)
- GET..: belongs to channel
- POST(List): belongs to channel
- PATCH(List): ADMIN || Owner; only changes Role
- DELETE: ADMIN || Owner; only admin cannot be deleted

### Article
- GET: belongs to channel
- POST: belongs to channel
- PUT: ADMIN || Owner
- DELETE: ADMIN || Owner



## issue
- Comment~
- Chat
- Like
- File
- Need test codes
- exception handling: id-NaN, Conflict