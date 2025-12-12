import 'package:flutter/material.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'NFC HCE Demo',
      home: Scaffold(
        appBar: AppBar(title: const Text('NFC HCE Demo')),
        body: const Padding(
          padding: EdgeInsets.all(20.0),
          child: Center(
            child: Text(
              'Buka aplikasi ini lalu tempelkan HP ke NFC reader (APDU / ISO-DEP).\n\nJika HP & reader kompatibel, payload akan dikirim oleh HCE service.',
              textAlign: TextAlign.center,
              style: TextStyle(fontSize: 16),
            ),
          ),
        ),
      ),
    );
  }
}
