// mongo-init.js
print('Start MongoDB init script...');

// Switch to admin database first
db = db.getSiblingDB('admin');

// Create the root user if it doesn't exist (though it should exist from env vars)
if (!db.getUser("root")) {
    db.createUser({
        user: "root",
        pwd: "medici",
        roles: ["root"]
    });
}

// Authenticate as root
db.auth('root', 'medici');
print('Authenticated as root user');

// Switch to medic_user_db database
db = db.getSiblingDB('medic_user_db');
print('Switched to medic_user_db');

// Drop existing user if exists
try {
    db.dropUser("app_user");
    print('Dropped existing app_user');
} catch (e) {
    print('No existing app_user to drop');
}

// Create app_user with correct permissions
db.createUser({
    user: 'app_user',
    pwd: 'medici',
    roles: [
        {
            role: 'readWrite',
            db: 'medic_user_db'
        },
        {
            role: 'dbAdmin',
            db: 'medic_user_db'
        }
    ]
});
print('Created app_user');

// Create collections
db.createCollection('users');
print('Created users collection');

// Create indexes if needed
db.users.createIndex({ "email": 1 }, { unique: true });
print('Created indexes');

print('MongoDB init script completed');