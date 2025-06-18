# SSL Setup Instructions

This application supports both HTTP and HTTPS modes. By default, it runs in HTTP mode.

## Running in HTTP Mode (Default)

The application is configured to run in HTTP mode by default. No additional setup is required.

## Running in HTTPS Mode

To enable HTTPS, follow these steps:

### Option 1: Using a Pre-Generated Keystore

1. Create a keystore file named `keystore.p12` and place it in the `src/main/resources` directory
2. Update the SSL settings in `application.properties`:
   ```properties
   # SSL configuration
   app.ssl.enabled=true
   server.ssl.key-store=classpath:keystore.p12
   server.ssl.key-store-password=yourpassword
   server.ssl.key-store-type=PKCS12
   server.ssl.key-alias=youralias
   ```
3. Restart the application

### Option 2: Generating a Keystore Using KeyTool (Recommended)

1. Ensure you have Java installed and the `keytool` utility is available
2. Run the following command from the project root directory:

   ```bash
   keytool -genkeypair -alias youralias -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore src/main/resources/keystore.p12 -validity 3650 -storepass yourpassword
   ```
   
   You'll be prompted to provide information for the certificate.

3. Update the SSL settings in `application.properties`:
   ```properties
   # SSL configuration
   app.ssl.enabled=true
   server.ssl.key-store=classpath:keystore.p12
   server.ssl.key-store-password=yourpassword
   server.ssl.key-store-type=PKCS12
   server.ssl.key-alias=youralias
   ```

4. Restart the application

## Troubleshooting

If you encounter SSL-related errors:

1. Verify that the keystore file exists in the `src/main/resources` directory
2. Ensure the password and alias in `application.properties` match the ones used when creating the keystore
3. If you're still having issues, try running in HTTP mode by setting `app.ssl.enabled=false`

## Notes for Development

- For local development, you can disable SSL by setting `app.ssl.enabled=false` in your application.properties
- For production, always use a properly signed certificate from a trusted certificate authority 