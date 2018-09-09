from djangohttp import HttpResponse

def index(request):
    return HttpResponse('Halo!')
