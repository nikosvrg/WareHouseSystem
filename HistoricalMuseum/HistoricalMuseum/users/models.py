from django.db import models
from django.contrib.auth.models import User
from django.templatetags.static import static
from PIL import Image
import datetime



GENDER_CHOICES = [
    ('M', 'Male'),
    ('F', 'Female')
]


class Student(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    image = models.ImageField(upload_to='profile_pics', null=True)
    first_name = models.CharField(default="", max_length=50)
    last_name = models.CharField(default="", max_length=50)
    birthday = models.DateField(null=True, blank=True, help_text="YYYY-MM-DD")
    gender = models.CharField(choices=GENDER_CHOICES,max_length=1)

    hero_studying = models.PositiveIntegerField(default=1)
    test_taking = models.PositiveIntegerField(default=1)

    def save(self, *args, **kwargs):
        super(Student, self).save(*args, **kwargs)
        img = Image.open(self.image.path)

        if img.height > 300 or img.width > 300:
            output_size = (300,300)
            img.thumbnail(output_size)
            img.save(self.image.path)


    def age(self):
        if self.birthday is None:
            pass
        else:
         return int((datetime.date.today() - self.birthday).days / 365.25)

    def __str__(self):
        return f'{self.user.username} Student'


