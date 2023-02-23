import boto3
import os
import subprocess
import uuid

# create an sqs client
sqs = boto3.client('sqs', region_name= 'us-east-1')

# create a s3 client
s3 = boto3.client('s3', region_name= 'us-east-1')

INPUT_IMAGE_DIR = 'input_images'

request_queue_url = 'https://sqs.us-east-1.amazonaws.com/702729433599/cse546-p1-request'
response_queue_url = 'https://sqs.us-east-1.amazonaws.com/702729433599/cse546-p1-response'

input_bucket_name = 'serverlesspresso.cse546.p1.requests'
output_bucket_name = 'serverlesspresso.cse546.p1.responses'

# Make dir to store input image
if not os.path.exists("input_images"):
	os.makedirs("input_images")

count = 0
while True:
	
	image_request = sqs.receive_message(QueueUrl=request_queue_url, MaxNumberOfMessages=1, WaitTimeSeconds=10)

	if 'Messages' not in image_request.keys():
		print("Message not found..")
		continue

	
	input_image = image_request['Messages'][0]['Body']
	count += 1
	print("Message is found..", str(input_image))
	print(count)
	
	IMAGE_PATH = INPUT_IMAGE_DIR + '/' + input_image

	s3.download_file(input_bucket_name, input_image, IMAGE_PATH)

	model_prediction = subprocess.check_output(['python3', 'image_classification.py', IMAGE_PATH]).decode('ASCII')
	
	os.remove(IMAGE_PATH)

	sqs.delete_message(QueueUrl=request_queue_url, ReceiptHandle=image_request['Messages'][0]['ReceiptHandle'])

	unique_id = uuid.uuid4()

	sqs.send_message(QueueUrl=response_queue_url, MessageBody=model_prediction)

	s3.put_object(Body=model_prediction, Bucket=output_bucket_name, Key=input_image)




