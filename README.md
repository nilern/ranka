# Ranka

> Rankat ankat rakentaa nuo bunkkerinsa betonista.
> 
> -- YUP, Rankat ankat

ClojureScript frontend framework.

## Design Notes

* Use Reitit frontend Controllers
    - Input routes have Clip config maps instead of Controller `:start`/`:stop`
    - For each Controller generate `:start` & `:stop` that use Clip `start` & `stop` to create/decommission a system
      with the config map. Merge the system into the parent Controller's system.
    - Generate Reitit router.
* An event has a keyword id and some arguments
    - Look the handler up from Controller system
    - Call the handler with the arguments
    - Handler should return a function that expects the system and returns a Taksi Task
        - Taksi TODO: complect a Reader monad into Task
    - Call the returned function with system and run the Task asynchronously
    - Like re-frame, handle a batch of events at a time. Chain the returned tasks and set off the chain
      to run asynchronously. This achieves a global ordering of side effects (unlike re-frame).
* Subscriptions are similar to events, but just return a (reactive / `IWatchable`) query result immediately
    - Subscription handler is called with the system and the sub args
    - Subscription caches are per-Controller
    - Subscriptions that are not hooked up to the DOM are inactive (derived subs stop computing)
