from cryptography.hazmat.primitives.ciphers.aead import AESGCM
import os
import base64


class CryptoService:
    def __init__(self, key: bytes = None):
        # AESGCM requires 128, 192, or 256-bit keys (16, 24, 32 bytes)
        self.key = key or AESGCM.generate_key(bit_length=256)
        self.aesgcm = AESGCM(self.key)

    def encrypt(self, plaintext: str, associated_data: str = "") -> str:
        nonce = os.urandom(12)  # 96-bit nonce for AES-GCM
        data = plaintext.encode("utf-8")
        aad = associated_data.encode("utf-8")

        ciphertext = self.aesgcm.encrypt(nonce, data, aad)
        result = base64.b64encode(nonce + ciphertext).decode("utf-8")
        return result

    def decrypt(self, encoded: str, associated_data: str = "") -> str:
        raw = base64.b64decode(encoded)
        nonce = raw[:12]
        ciphertext = raw[12:]
        aad = associated_data.encode("utf-8")

        plaintext = self.aesgcm.decrypt(nonce, ciphertext, aad)
        return plaintext.decode("utf-8")

    def get_key_b64(self) -> str:
        return base64.b64encode(self.key).decode("utf-8")

    @staticmethod
    def from_b64_key(b64_key: str) -> "CryptoService":
        key = base64.b64decode(b64_key)
        return CryptoService(key)
