import qrcode

def generar_qr():
   
    numero = input("Ingrese número de la tarjeta: ")

    qr = qrcode.QRCode(
        version=1, 
        error_correction=qrcode.constants.ERROR_CORRECT_L, 
        box_size=10,  
        border=4,  
    )

    qr.add_data(numero)
    qr.make(fit=True)

    img = qr.make_image(fill_color="black", back_color="white")

    nombre_archivo = f"qr_{numero}.png"
    img.save(f"/home/tom/Pictures/{nombre_archivo}")

    print(f"\n✅ Código QR generado y guardado como: {nombre_archivo}")


generar_qr();