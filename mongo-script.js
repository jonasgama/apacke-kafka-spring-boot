let myUser="producer";
let myDb="producer-db";
let myPass="1234";

print(db.getSiblingDB(myDb))
print(db.products.insert({status:"on"}))

print(
  db.createUser(
    {
      user: myUser,
      pwd: myPass,
      roles: [ 
        { role: "userAdmin", db: myDb },
        { role: "readWrite", db: myDb }  
      ]
    }
  )
);