from ngram_score import ngram_score
fitness = ngram_score('/home/CocoKuma/mysite/english_quadgrams.txt')

def decrypt(layers, cipher_text):
    # If the user specifies only 1 rail then we just return the cipher_text 
    # because the text can't be transformed with 1 rail
    if (layers == 1):
        return cipher_text
        
    # Create the rail matrix which is a multidimensional array    
    railMatrix = [['#' for i in range(len(cipher_text))] for j in range(layers)]

    # Construct the path to follow
    rail = 0
    directionDown = True
    result = ''

    for i in range(0, len(cipher_text)):
        railMatrix[rail][i] = '.'

        if (rail == layers - 1): 
            directionDown = False
        elif (rail == 0):
            directionDown = True

        if (directionDown):
            rail = rail + 1
        else:
            rail = rail - 1
    
    # Now we begin to build the word into the path we created
    index = 0
    for i in range(0, layers):
        for j in range(0, len(cipher_text)):
            if (railMatrix[i][j] == '.' and index < len(cipher_text)):
                railMatrix[i][j] = cipher_text[index]
                index += 1
    
    # Read the fence
    rail = 0
    directionDown = True
    result = ''
    for i in range(len(cipher_text)):
        result = result + str(railMatrix[rail][i])

        if (rail == layers - 1):
            directionDown = False
        elif (rail == 0):
            directionDown = True
        
        if (directionDown):
            rail = rail + 1
        else:
            rail = rail - 1

    return result

def hack_railCipher(ciphertext):
    ciphertext = ciphertext.upper()
    plaintext = decrypt(1, ciphertext)
    parentscore = fitness.score(plaintext)
    
    for i in range (2, len(ciphertext) - 1):
        plaintext = decrypt(i, ciphertext)
        score = fitness.score(plaintext)
        #print(plaintext + ' ' + str(score))
        if score > parentscore:
            parentscore = score
            rail = i
    return(decrypt(rail, ciphertext))