#include "myHeader.h"
#include <msctf.h>

class MyUIElementSink : public ITfUIElementSink
{
public:
    HRESULT STDMETHODCALLTYPE QueryInterface(REFIID riid, void **ppvObject)
    {
        if (!ppvObject)
            return E_INVALIDARG;
        if (IsEqualGUID(riid, __uuidof(IUnknown)) || IsEqualGUID(riid, __uuidof(ITfUIElementSink)))
        {
            *ppvObject = this;
            this->AddRef();
            return S_OK;
        }
        else
        {
            *ppvObject = NULL;
            return E_NOINTERFACE;
        }
    }
    ULONG STDMETHODCALLTYPE AddRef(void)
    {
        return ++this->refcount;
    };
    ULONG STDMETHODCALLTYPE Release(void)
    {
        if (--this->refcount == 0)
        {
            free(this);
            return 0;
        }
        return this->refcount;
    };
    HRESULT STDMETHODCALLTYPE BeginUIElement(DWORD id, WINBOOL *show)
    {
        *show = true;
        asm volatile("inc %esi");//跳过下一个处理函数 libsdl
        return S_OK;
    }
    HRESULT STDMETHODCALLTYPE UpdateUIElement(DWORD id)
    {
        return S_OK;
    }
    HRESULT STDMETHODCALLTYPE EndUIElement(DWORD id)
    {
        return S_OK;
    }

private:
    int refcount = 1;
};
DWORD sinkCookie;

int setupCtf()
{
    ITfThreadMgr *threadMgr;
    TF_GetThreadMgr(&threadMgr);
    if (threadMgr == NULL)
    {
        log("Fail to get threadMgr");
        return false;
    }
    TfClientId tid;
    log("deactivate:" + std::to_string(SUCCEEDED(threadMgr->Deactivate())));
    log("activate: " + std::to_string(SUCCEEDED(threadMgr->Activate(&tid))));

    ITfSource *source;
    log("querySource: " + std::to_string(SUCCEEDED(threadMgr->QueryInterface(&source))));
    log("AdviseSink: " + std::to_string(SUCCEEDED(source->AdviseSink(__uuidof(ITfUIElementSink), new MyUIElementSink(), &sinkCookie))));
    source->Release();
    return true;
}