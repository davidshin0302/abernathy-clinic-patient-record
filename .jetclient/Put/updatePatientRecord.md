```toml
name = 'updatePatientRecord'
method = 'PUT'
url = 'http://localhost:8082/patHistory/update/1?index=0'
sortWeight = 1000000
id = '3260b37b-9e6f-4f0e-a721-5398254891d5'

[[queryParams]]
key = 'index'
value = '0'

[body]
type = 'JSON'
raw = '''
{
  "date": "2025-02-18",
  "note": "Patient: TestBorderline Practitioner's notes/recommendations: Patient states that they are feeling a great deal of stress at work Patient also complains that their hearing seems Abnormal as of late"
}'''
```
