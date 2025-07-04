from cryptography.hazmat.primitives.ciphers.aead import AESGCM
from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from base64 import urlsafe_b64encode, urlsafe_b64decode
import os

# === Symmetric Encryption (AES-GCM) ===

def generate_key(password: str, salt: bytes = None) -> bytes:
    salt = salt or os.urandom(16)
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(), length=32, salt=salt, iterations=100_000
    )
    key = kdf.derive(password.encode())
    return key, salt

def encrypt_data(data: bytes, key: bytes) -> bytes:
    aesgcm = AESGCM(key)
    nonce = os.urandom(12)
    encrypted = aesgcm.encrypt(nonce, data, None)
    return nonce + encrypted  # Combine nonce + ciphertext

def decrypt_data(encrypted: bytes, key: bytes) -> bytes:
    nonce = encrypted[:12]
    ciphertext = encrypted[12:]
    aesgcm = AESGCM(key)
    return aesgcm.decrypt(nonce, ciphertext, None)

# === Utility for encoding/decoding ===

def encrypt_string(plain_text: str, key: bytes) -> str:
    encrypted = encrypt_data(plain_text.encode(), key)
    return urlsafe_b64encode(encrypted).decode()

def decrypt_string(encrypted_b64: str, key: bytes) -> str:
    encrypted = urlsafe_b64decode(encrypted_b64.encode())
    return decrypt_data(encrypted, key).decode()
