
# A very simple Flask Hello World app for you to get started with...

from flask import Flask, request
from vigenereDecrypt import hackVigenereDictionary
from railDecrypt import hack_railCipher

app = Flask(__name__)

@app.route('/')
def home_page():
    ciphertext = request.args.get('ciphertext')
    result = hackVigenereDictionary(ciphertext)
    return result

@app.route('/rail')
def rail():
    ciphertext = request.args.get('cipher')
    result = hack_railCipher(ciphertext)
    return result