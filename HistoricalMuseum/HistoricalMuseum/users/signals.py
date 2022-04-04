from django.db.models.signals import post_save, pre_save
from django.contrib.auth.models import User
from django.dispatch import receiver
from .models import Student



def create_profile(sender, instance, created, **kwargs):
    if created:
        Student.objects.create(user=instance)


post_save.connect(create_profile,sender=User)


#Change avatar based on gender

def set_avatar(instance):
    avatar = instance.image
    gender = instance.gender
    if gender == 'M':
        avatar = 'profile_pics/boy.png'
    elif gender == 'F':
        avatar = 'profile_pics/girl.png'
    else:
        avatar = 'profile_pics/default.jpg'
    return avatar

def save_profile(sender, instance, *args, **kwargs):
    if not instance.image:
        instance.image = set_avatar(instance)
    else:
        instance.image = set_avatar(instance)
pre_save.connect(save_profile, sender=Student)


