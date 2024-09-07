from .base_command import BaseCommannd
from openpyxl import Workbook
from faker import Faker
import pyzipper
import io

class Generatereport(BaseCommannd):
    def __init__(self):
        self.key = "empty"
        self.fake = Faker()

    def create_excel_file(self):
        # Crear una hoja de cálculo usando openpyxl
        wb = Workbook()
        ws = wb.active
        ws.title = "Reporte"
        
        # Agregar encabezados
        ws.append(["Nombre", "Numero incidente", "Fecha creacion", "Descripcion", "Origen"])

        # Generar datos falsos
        for _ in range(1000):  # Cambia el número según la cantidad de datos que necesites
            nombre_cliente = self.fake.name()
            numero_incidente = self.fake.random_int(min=1000, max=9999)  
            descripcion_incidente = self.fake.text(max_nb_chars=100) 
            origen_aplicacion = self.fake.random_element(elements=("Web", "Móvil"))
            fecha_creacion = self.fake.date_this_decade()
            
            ws.append([nombre_cliente, numero_incidente, fecha_creacion, descripcion_incidente, origen_aplicacion])
            
        # Guardar el archivo de Excel en un buffer en memoria
        file_stream = io.BytesIO()
        wb.save(file_stream)
        file_stream.seek(0)
        
        return file_stream
    
    def protect_with_password(self, file_stream, password):
        # Proteger el archivo de Excel dentro de un archivo zip con contraseña
        zip_buffer = io.BytesIO()
        with pyzipper.AESZipFile(zip_buffer, 'w', compression=pyzipper.ZIP_DEFLATED, encryption=pyzipper.WZ_AES) as zf:
            zf.setpassword(password.encode())
            zf.writestr('reporte.xlsx', file_stream.getvalue())
        
        zip_buffer.seek(0)
        return zip_buffer

    def execute(self):
            file_stream = self.create_excel_file()
            # Proteger el archivo con una contraseña
            password = "qwerty"
            zip_buffer = self.protect_with_password(file_stream, password)

            return zip_buffer
    