from pycipher import Vigenere
from ngram_score import ngram_score
fitness = ngram_score('/home/CocoKuma/mysite/english_quadgrams.txt')

def loadDictionary():
    dictionaryFile = open('/home/CocoKuma/mysite/dictionary.txt')
    englishWords = {}
    for word in dictionaryFile.read().split('\n'):
        englishWords[word] = None
    dictionaryFile.close()
    return englishWords

def hackVigenereDictionary(ciphertext):
    ENGLISH_WORDS = loadDictionary()
    plaintext = Vigenere(next(iter(ENGLISH_WORDS))).decipher(ciphertext)
    parentscore = fitness.score(plaintext)

    for key in ENGLISH_WORDS:
        plaintext = Vigenere(key).decipher(ciphertext)
        score = fitness.score(plaintext)
        if (score > parentscore):
            parentscore = score
            resultKey = key
            #print(plaintext)

    return resultKey


#print(hackVigenereDictionary("jopqjhtwvsaiqxk vn uydcipq loiu mzhlnofne cxv denmgte vy loe eymysg! dzps hvsn iu wq_mitcl_jrazlv"))
